package com.baomidou.springboot.sql;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.expr.SQLAggregateExpr;
import com.alibaba.druid.sql.ast.expr.SQLAllColumnExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLCharExpr;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLMethodInvokeExpr;
import com.alibaba.druid.sql.ast.expr.SQLNumericLiteralExpr;
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr;
import com.alibaba.druid.sql.ast.expr.SQLTextLiteralExpr;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableItem;
import com.alibaba.druid.sql.ast.statement.SQLColumnDefinition;
import com.alibaba.druid.sql.ast.statement.SQLCreateStatement;
import com.alibaba.druid.sql.ast.statement.SQLDropTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.ast.statement.SQLJoinTableSource;
import com.alibaba.druid.sql.ast.statement.SQLLateralViewTableSource;
import com.alibaba.druid.sql.ast.statement.SQLSelectItem;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.ast.statement.SQLSubqueryTableSource;
import com.alibaba.druid.sql.ast.statement.SQLTableSource;
import com.alibaba.druid.sql.ast.statement.SQLUnionQueryTableSource;
import com.alibaba.druid.sql.ast.statement.SQLValuesTableSource;
import com.alibaba.druid.sql.ast.statement.SQLWithSubqueryClause;
import com.alibaba.druid.sql.dialect.hive.ast.HiveInsertStatement;
import com.alibaba.druid.sql.dialect.hive.stmt.HiveCreateTableStatement;
import com.alibaba.druid.sql.dialect.hive.visitor.HiveSchemaStatVisitor;
import com.alibaba.druid.sql.parser.Token;
import com.alibaba.druid.util.FnvHash;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * 合并 SQLTableAliasCollectVisitor 、 SQLSubQueryGroupVisitor
 *
 * @author linzhenlie-jk
 * @date 2023/4/3
 */
@Slf4j
public class HiveRuleCheckVisitor extends HiveSchemaStatVisitor {
    public static final String ENCRYPT_SUFFIX = "_encryptx";

    protected Map<SQLTableSource, List<String>> tableSource2AliasMap = new LinkedHashMap<>();

    protected Map<Long, List<SQLSubqueryTableSource>> subQueryGroupMap = new LinkedHashMap<>();

    List<SQLExprTableSource> tableList = new LinkedList<>();

    List<SQLJoinTableSource.JoinType> joinTypeList = new LinkedList<>();

    private AtomicInteger constantCnt = new AtomicInteger(0);

    private Map<String, AtomicInteger> ownerCnt = new HashMap<>(20);

    private Map<String, AtomicInteger> methodCnt = new HashMap<>(20);

    private Map<String, AtomicInteger> distinctCnt = new HashMap<>(20);

    public boolean visit(SQLDropTableStatement x) {
        super.visit(x);
        return false;
    }

    public void endVisit(SQLDropTableStatement drop) {
        if (!drop.isIfExists()) {
            log.error("drop操作必须指定IF EXISTS");
        }
    }

    public boolean visit(HiveInsertStatement x) {
        super.visit(x);
        return false;
    }

    public void endVisit(HiveInsertStatement x) {
        if (!x.isOverwrite()) {
            log.error("insert操作必须指定overwrite");
        }
    }

    public boolean visit(HiveCreateTableStatement x) {
        super.visit(x);
        return true;
    }

    public void endVisit(HiveCreateTableStatement x) {
        if (!x.isIfNotExists()) {
            log.error("表信息:create操作必须指定IF NOT EXISTS");
        }

        SQLExpr orcStored = ObjectUtils.firstNonNull(x.getStoredAs(), x.getUsing());
        if (Objects.isNull(orcStored)) {
            log.error("create操作必须指定STORED AS ORC 或 USING ORC");
        }
    }

    public void endVisit(SQLLateralViewTableSource x) {
        String alias = x.getAlias();
        if (alias == null) {
            return;
        }
        tableSource2AliasMap.computeIfAbsent(x, key -> new ArrayList<>()).add(alias);
    }


    public void endVisit(SQLSubqueryTableSource x) {
        String alias = x.getAlias();
        if (alias == null) {
            return;
        }

        String sql = SQLUtils.toSQLString(x.getSelect(), dbType);
        long hashCode64 = FnvHash.fnv1a_64(sql);
        List<SQLSubqueryTableSource> list = subQueryGroupMap.get(hashCode64);
        if (list == null) {
            list = new ArrayList<SQLSubqueryTableSource>();
            subQueryGroupMap.put(hashCode64, list);
        }
        list.add(x);

        tableSource2AliasMap.computeIfAbsent(x, key -> new ArrayList<>()).add(alias);
    }

    public boolean visit(SQLWithSubqueryClause.Entry x) {
        return false;
    }

    public void endVisit(SQLWithSubqueryClause.Entry x) {
        String alias = x.getAlias();
        if (alias == null) {
            return;
        }
        tableSource2AliasMap.computeIfAbsent(x, key -> new ArrayList<>()).add(alias);
    }

    public boolean visit(SQLUnionQueryTableSource x) {
        super.visit(x);
        return true;
    }

    public void endVisit(SQLUnionQueryTableSource x) {
        String alias = x.getAlias();
        if (alias == null) {
            return;
        }

        tableSource2AliasMap.computeIfAbsent(x, key -> new ArrayList<>()).add(alias);
    }

    public boolean visit(SQLJoinTableSource x) {
        super.visit(x);
        return true;
    }

    public void endVisit(SQLJoinTableSource x) {
        joinTypeList.add(x.getJoinType());
        String alias = x.getAlias();
        if (alias == null) {
            return;
        }
        tableSource2AliasMap.computeIfAbsent(x, key -> new ArrayList<>()).add(alias);
    }

    public boolean visit(SQLExprTableSource x) {
        super.visit(x);
        return false;
    }

    public void endVisit(SQLExprTableSource x) {
        tableList.add(x);
        String alias = x.getAlias();
        if (StringUtils.isEmpty(alias)) {
            SQLExpr expr = x.getExpr();
            if (x.getParent() instanceof SQLJoinTableSource) {
                log.info("表:{},未指定别名", x);
            }
            if (expr instanceof SQLName) {
                tableSource2AliasMap.computeIfAbsent(x, key -> new ArrayList<>()).add(((SQLName) expr).getSimpleName());
                return;
            }
            return;
        }
        tableSource2AliasMap.computeIfAbsent(x, key -> new ArrayList<>()).add(alias);
    }

    public boolean visit(SQLValuesTableSource x) {
        super.visit(x);
        return false;
    }

    public void endVisit(SQLValuesTableSource x) {
        String alias = x.getAlias();
        if (alias == null) {
            return;
        }
        tableSource2AliasMap.computeIfAbsent(x, key -> new ArrayList<>()).add(alias);
    }

    public boolean visit(SQLSelectQueryBlock x) {
        super.visit(x);
        return false;
    }

    public void endVisit(SQLSelectQueryBlock queryBlock) {
        if (queryBlock.isDistinct()) {
            // select distinct loan_no FROM A
            log.error("预估数据未来会超千万,并且select字段超过1个且存在distinct操作时,将distinct对应字段下沉到group by");
        }

        if (queryBlock.getSelectList().size() > 1 && distinctCnt.size() >= 1) {
            log.error("select字段超过1个且存在distinct操作时,使用group by替代distinct");
        }
    }


    public boolean visit(SQLColumnDefinition columnDefinition) {
        super.visit(columnDefinition);
        return false;
    }

    public void endVisit(SQLColumnDefinition columnDefinition) {
        String logPrefix = "";
        if (columnDefinition.getParent() instanceof SQLAlterTableItem) {
            logPrefix = "表变更信息:";
        }
        if (columnDefinition.getParent() instanceof SQLCreateStatement) {
            logPrefix = "建表信息:";
        }

        if (Objects.isNull(columnDefinition.getDataType())) {
            log.error(logPrefix + columnDefinition.getName() + "必须指定数据类型");
        }
        SQLCharExpr columnComment = (SQLCharExpr) columnDefinition.getComment();
        if (Objects.nonNull(columnComment) && StringUtils.isBlank(columnComment.getText())) {
            log.error(logPrefix + columnDefinition.getName() + "必须填写COMMENT注释");
        }
    }

    public boolean visit(SQLAllColumnExpr x) {
        // 如果是要做字段血缘,此处可以将字段捞回填充,进行各字段accept
        super.visit(x);
        return true;
    }

    public void endVisit(SQLAllColumnExpr allColumnExpr) {
        // select *,loan_no AS loan_no1 FROM A
        log.error("禁止使用*获取所有字段");
    }

    public boolean visit(SQLCharExpr x) {
        super.visit(x);
        return true;
    }

    public void endVisit(SQLCharExpr charExpr) {
    }

    // 没有这个方法
    public void endVisit(SQLNumericLiteralExpr variantRefExpr) {

    }

    public boolean visit(SQLPropertyExpr x) {
        super.visit(x);
        return true;
    }

    public void endVisit(SQLPropertyExpr propertyExpr) {
        // select t.*,loan_no AS loan_no1 FROM A AS t
        if (Token.STAR.name.equals(propertyExpr.getName())) {
            log.error("禁止使用*获取所有字段");
        }

        if (StringUtils.endsWith(propertyExpr.getName(), ENCRYPT_SUFFIX)) {
            log.error("敏感字段限制使用:{}", propertyExpr.getName());
        }
    }


    public boolean visit(SQLIdentifierExpr idExpr) {
        super.visit(idExpr);
        return false;
    }

    public void endVisit(SQLIdentifierExpr idExpr) {
        // 只有在建表时校验
        if (idExpr.getParent() instanceof HiveCreateTableStatement && !idExpr.nameEquals(Token.USING.name) && !idExpr.nameEquals("ORC")) {
            log.error("create操作必须指定STORED AS ORC 或 USING ORC");
        }

        if (StringUtils.endsWith(idExpr.getName(), ENCRYPT_SUFFIX)) {
            log.error("敏感字段限制使用:{}", idExpr.getName());
        }

        if (idExpr.nameEquals(Token.STAR.name)) {
            log.error("禁止使用*获取所有字段");
        }

        // where 条件未指定引用表名
        if (idExpr.getParent() instanceof  SQLBinaryOpExpr) {
            log.error(idExpr.getParent() + ":未指定引用表别名");
        } else {
            //ownerCnt.computeIfAbsent(idExpr.getOwner().toString(), key -> new AtomicInteger(0)).incrementAndGet();
        }
    }

    public boolean visit(SQLMethodInvokeExpr x) {
        super.visit(x);
        return true;
    }

    public void endVisit(SQLMethodInvokeExpr methodInvokeExpr) {
        // where条件中 date(a.time_submit) >'2021-01-01' 不会使用到别名
        if (methodInvokeExpr.getParent() instanceof SQLSelectItem && StringUtils.isBlank(((SQLSelectItem) methodInvokeExpr.getParent()).getAlias())) {
            log.error("未使用别名:{}", SQLUtils.toSQLString(methodInvokeExpr));
        }
        methodCnt.computeIfAbsent(methodInvokeExpr.getMethodName().toLowerCase(), key -> new AtomicInteger(0)).incrementAndGet();
    }

    public boolean visit(SQLAggregateExpr x) {
        super.visit(x);
        return true;
    }

    public void endVisit(SQLAggregateExpr aggregateExpr) {
        if (aggregateExpr.isDistinct()) {
            log.warn("聚合函数出现distinct部分:{}", aggregateExpr);
        }

        /*List<SQLExpr> argList = aggregateExpr.getArguments();
        for (SQLExpr expr : argList) {
            // 重得除了方法外的列循环
            sqlExpr(expr, alias, false, cntVO);
        }*/
    }

    public boolean visit(SQLBinaryOpExpr opExpr) {
        super.visit(opExpr);
        return true;
    }

    public void endVisit(SQLBinaryOpExpr opExpr) {
        if (opExpr.getParent() instanceof SQLJoinTableSource) {
            SQLExpr left = opExpr.getLeft();
            SQLExpr right = opExpr.getRight();
            if (left instanceof SQLBinaryOpExpr) {
                SQLBinaryOpExpr leftBin = (SQLBinaryOpExpr) left;
                SQLExpr leftExpr = leftBin.getLeft();
                SQLExpr rightExpr = leftBin.getRight();
                if (validateConstant(leftExpr) && validateConstant((rightExpr))) {
                    log.info("禁止使用常量条件等式");
                }
            }

            if (left instanceof SQLBinaryOpExpr) {
                SQLBinaryOpExpr leftBin = (SQLBinaryOpExpr) left;
                SQLExpr leftExpr = leftBin.getLeft();
                SQLExpr rightExpr = leftBin.getRight();
                if (validateConstant(leftExpr) && validateConstant((rightExpr))) {
                    log.info("禁止使用常量条件等式");
                }
            }
        }
    }

    private boolean validateConstant(SQLExpr sqlExpr) {
        if (sqlExpr instanceof SQLNumericLiteralExpr || sqlExpr instanceof SQLTextLiteralExpr) {
            return true;
        }
        return false;
    }

    public List<SQLExprTableSource> getTableList(){
        return tableList;
    }

    public List<String> getTableNameList(){
        return tableList.stream().map(tableSource -> tableSource.getSchema()+"."+tableSource.getTableName()).collect(Collectors.toList());
    }
}
