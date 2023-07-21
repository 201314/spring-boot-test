package com.baomidou.springboot.sql;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLAggregateExpr;
import com.alibaba.druid.sql.ast.expr.SQLAllColumnExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLCharExpr;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLInSubQueryExpr;
import com.alibaba.druid.sql.ast.expr.SQLMethodInvokeExpr;
import com.alibaba.druid.sql.ast.expr.SQLNumericLiteralExpr;
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr;
import com.alibaba.druid.sql.ast.expr.SQLVariantRefExpr;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableAddColumn;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableAlterColumn;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableItem;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableReplaceColumn;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLColumnDefinition;
import com.alibaba.druid.sql.ast.statement.SQLDropTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.ast.statement.SQLJoinTableSource;
import com.alibaba.druid.sql.ast.statement.SQLLateralViewTableSource;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.ast.statement.SQLSelectItem;
import com.alibaba.druid.sql.ast.statement.SQLSelectQuery;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.ast.statement.SQLSubqueryTableSource;
import com.alibaba.druid.sql.ast.statement.SQLTableSource;
import com.alibaba.druid.sql.ast.statement.SQLUnionQuery;
import com.alibaba.druid.sql.ast.statement.SQLUnionQueryTableSource;
import com.alibaba.druid.sql.dialect.hive.ast.HiveInsertStatement;
import com.alibaba.druid.sql.dialect.hive.parser.HiveStatementParser;
import com.alibaba.druid.sql.dialect.hive.stmt.HiveCreateTableStatement;
import com.alibaba.druid.sql.dialect.hive.visitor.HiveSchemaStatVisitor;
import com.alibaba.druid.sql.parser.Token;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * @author linzhenlie-jk
 * @date 2023/3/23
 */
@Slf4j
public class HiveSqlUtil {
    public static final String ENCRYPT_SUFFIX = "_encryptx";

    public static void parser1(String sql) {
        HiveStatementParser parser = new HiveStatementParser(sql);
        List<SQLStatement> stmt = parser.parseStatementList();
        HiveRuleCheckVisitor checkVisitor = new HiveRuleCheckVisitor();
        stmt.forEach(sqlStatement -> {
            sqlStatement.accept(checkVisitor);
        });
    }

    public static CntVO parser(String sql) {
        HiveStatementParser parser = new HiveStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();

        CntVO cntVO = new CntVO();
        for (SQLStatement stmt : stmtList) {
            if (stmt instanceof SQLDropTableStatement) {
                SQLDropTableStatement drop = (SQLDropTableStatement) stmt;
                if (!drop.isIfExists()) {
                    log.error("drop操作必须指定IF EXISTS");
                }
                continue;
            }

            if (stmt instanceof SQLAlterTableStatement) {
                SQLAlterTableStatement alter = (SQLAlterTableStatement) stmt;
                List<SQLColumnDefinition> alterList = new ArrayList<>();
                for (SQLAlterTableItem alterItem : alter.getItems()) {
                    if (alterItem instanceof SQLAlterTableAlterColumn) {
                        SQLAlterTableAlterColumn alterColumn = (SQLAlterTableAlterColumn) alterItem;
                        alterList.add(alterColumn.getColumn());
                    }
                    if (alterItem instanceof SQLAlterTableAddColumn) {
                        SQLAlterTableAddColumn addColumn = (SQLAlterTableAddColumn) alterItem;
                        alterList.addAll(addColumn.getColumns());
                    }
                    if (alterItem instanceof SQLAlterTableReplaceColumn) {
                        SQLAlterTableReplaceColumn replaceColumn = (SQLAlterTableReplaceColumn) alterItem;
                        alterList.addAll(replaceColumn.getColumns());
                    }
                }
                checkColumn(alterList, "表变更信息:");
            }

            if (stmt instanceof HiveInsertStatement) {
                HiveInsertStatement insert = (HiveInsertStatement) stmt;
                if (!insert.isOverwrite()) {
                    log.error("insert操作必须指定overwrite");
                }
                selectQueryMethod(insert.getQuery(), cntVO);
                continue;
            }

            if (stmt instanceof HiveCreateTableStatement) {
                HiveCreateTableStatement create = (HiveCreateTableStatement) stmt;
                if (!create.isIfNotExists()) {
                    log.error("表信息:create操作必须指定IF NOT EXISTS");
                }

                SQLCharExpr comment = (SQLCharExpr) create.getComment();
                if (Objects.nonNull(comment) && StringUtils.isBlank(comment.getText())) {
                    //log.error(logPrefix + columnDefinition.getName() + "必须填写COMMENT注释");
                }

                SQLExpr orcStored = ObjectUtils.firstNonNull(create.getStoredAs(), create.getUsing());
                // 只有在建表时校验
                if (Objects.isNull(orcStored)) {
                    log.error("表信息:create操作必须指定STORED AS ORC或 USING ORC");
                } else {
                    sqlExpr(orcStored, create.getTableName(), false, cntVO);
                }
                selectQueryMethod(create.getSelect(), cntVO);

                checkColumn(create.getColumnDefinitions(), "字段信息:");
                checkColumn(create.getPartitionColumns(), "分区信息:");
            }
        }
        cntVO.check();
        return cntVO;
    }

    public static void selectQueryMethod(SQLSelect sqlSelect, CntVO cntVO) {
        if (Objects.isNull(sqlSelect)) {
            return;
        }
        selectQueryMethod(sqlSelect.getQuery(), cntVO);
    }

    public static void selectQueryMethod(SQLSelectQuery query, CntVO cntVO) {
        if (Objects.isNull(query)) {
            return;
        }

        if (query instanceof SQLUnionQuery) {
            SQLUnionQuery unQuery = (SQLUnionQuery) query;
            unQuery.getRelations().stream().forEach(sqlSelectQuery -> selectQueryMethod(sqlSelectQuery, cntVO));
        } else {
            SQLSelectQueryBlock queryBlock = (SQLSelectQueryBlock) query;
            selectQueryMethod(queryBlock, cntVO);
        }
    }

    private static void selectQueryMethod(SQLSelectQueryBlock queryBlock, CntVO cntVO) {
        if (Objects.isNull(queryBlock)) {
            return;
        }

        if (queryBlock.isDistinct()) {
            // select distinct loan_no FROM A
            log.error("预估数据未来会超千万,并且select字段超过1个且存在distinct操作时,将distinct对应字段下沉到group by");
        }

        for (SQLSelectItem sqlSelectItem : queryBlock.getSelectList()) {
            sqlExpr(sqlSelectItem.getExpr(), sqlSelectItem.getAlias(), false, cntVO);
        }

        if (queryBlock.getSelectList().size() > 1 && cntVO.getDistinctCnt().size() >= 1) {
            log.error("select字段超过1个且存在distinct操作时,使用group by替代distinct");
        }

        // 子查询
        recuriseTable(queryBlock.getFrom(), cntVO);

        // where in (select * from A)
        SQLExpr sqlExpr = queryBlock.getWhere();
        if (sqlExpr instanceof SQLInSubQueryExpr) {
            SQLInSubQueryExpr inSubQueryExpr = (SQLInSubQueryExpr) sqlExpr;
            selectQueryMethod(inSubQueryExpr.getSubQuery(), cntVO);
        } else {
            sqlExpr(sqlExpr, null, false, cntVO);
        }
    }

    private static void checkColumn(List<SQLColumnDefinition> columnDefinitionList, String logPrefix) {
        for (SQLColumnDefinition column : columnDefinitionList) {
            if (Objects.isNull(column.getDataType())) {
                log.error(logPrefix + column.getName() + "必须指定数据类型");
            }
            SQLCharExpr columnComment = (SQLCharExpr) column.getComment();
            if (Objects.nonNull(columnComment) && StringUtils.isBlank(columnComment.getText())) {
                log.error(logPrefix + column.getName() + "必须填写COMMENT注释");
            }
        }
    }

    public static void sqlExpr(SQLExpr sqlExpr, String alias, boolean joinCondition, CntVO cntVO) {
        if (sqlExpr instanceof SQLVariantRefExpr) {
            // 占位符
            SQLVariantRefExpr variantRefExpr = (SQLVariantRefExpr) sqlExpr;
            return;
        }

        if (sqlExpr instanceof SQLAllColumnExpr) {
            // select *,loan_no AS loan_no1 FROM A
            log.error("禁止使用*获取所有字段");
        }

        if (sqlExpr instanceof SQLNumericLiteralExpr || sqlExpr instanceof SQLCharExpr) {
            cntVO.getConstantCnt().incrementAndGet();
        }

        if (sqlExpr instanceof SQLBinaryOpExpr) { //a.pday >= '20220101' AND date(a.time_submit) >= '2022-01-01'
            SQLBinaryOpExpr opExpr = (SQLBinaryOpExpr) sqlExpr;
            CntVO leftVO = new CntVO();
            CntVO rightVO = new CntVO();
            sqlExpr(opExpr.getLeft(), null, false, leftVO);
            sqlExpr(opExpr.getRight(), null, false, rightVO);

            if (leftVO.getConstantCnt().get() > 0 && leftVO.getConstantCnt().get() == rightVO.getConstantCnt().get()) {
                log.error("使用了常量条件:{}", opExpr);
            }
            cntVO.getDistinctCnt().putAll(leftVO.getDistinctCnt());
            cntVO.getDistinctCnt().putAll(rightVO.getDistinctCnt());

            cntVO.getMethodCnt().putAll(leftVO.getMethodCnt());
            cntVO.getMethodCnt().putAll(rightVO.getMethodCnt());

            if (sqlExpr.getParent() instanceof SQLSelectQueryBlock) {
                // 把关联条件放在此处
                cntVO.getConditionList().add(sqlExpr);
            }

            if (joinCondition) {
                cntVO.getOwnerCnt().forEach((key, atomicInteger) -> {
                    if (leftVO.getOwnerCnt().containsKey(key)) {
                        atomicInteger.addAndGet(leftVO.getOwnerCnt().get(key).get());
                        leftVO.getOwnerCnt().remove(key);
                    }
                    if (rightVO.getOwnerCnt().containsKey(key)) {
                        atomicInteger.addAndGet(rightVO.getOwnerCnt().get(key).get());
                        rightVO.getOwnerCnt().remove(key);
                    }
                });
                cntVO.getOwnerCnt().putAll(leftVO.getOwnerCnt());
                cntVO.getOwnerCnt().putAll(rightVO.getOwnerCnt());
            }
            leftVO.clean();
            rightVO.clean();
        }

        if (sqlExpr instanceof SQLIdentifierExpr) {
            // 没有使用别名时,会进入此处,唯一属性
            SQLIdentifierExpr idExpr = (SQLIdentifierExpr) sqlExpr;
            if (idExpr.nameEquals(Token.STAR.name)) {
                log.error("禁止使用*获取所有字段");
            }

            if (StringUtils.endsWith(idExpr.getName(), ENCRYPT_SUFFIX)) {
                log.error("敏感字段限制使用:" + idExpr.getName());
            }

            // 只有在建表时校验
            if (sqlExpr.getParent() instanceof HiveCreateTableStatement && !idExpr.nameEquals(Token.USING.name) && !idExpr.nameEquals("ORC")) {
                log.error("表信息:create操作必须指定STORED AS ORC或 USING ORC");
            }
        }

        if (sqlExpr instanceof SQLPropertyExpr) {
            // select t.*,loan_no AS loan_no1 FROM A AS t
            SQLPropertyExpr propertyExpr = (SQLPropertyExpr) sqlExpr;
            if (Token.STAR.name.equals(propertyExpr.getName())) {
                log.error("禁止使用*获取所有字段");
            }

            if (StringUtils.endsWith(propertyExpr.getName(), ENCRYPT_SUFFIX)) {
                log.error("敏感字段限制使用:{}", propertyExpr.getName());
            }

            if (Objects.isNull(propertyExpr.getOwner())) {
                log.error(propertyExpr + ":未指定引用表别名");
            } else {
                cntVO.getOwnerCnt().computeIfAbsent(propertyExpr.getOwner().toString(), key -> new AtomicInteger(0)).incrementAndGet();
            }
        }

        if (sqlExpr instanceof SQLMethodInvokeExpr) {
            SQLMethodInvokeExpr methodInvokeExpr = (SQLMethodInvokeExpr) sqlExpr;
            // where条件中 date(a.time_submit) >'2021-01-01' 不会使用到别名
            if (sqlExpr.getParent() instanceof SQLSelectItem && StringUtils.isBlank(alias)) {
                log.error(sqlExpr + ":未使用别名");
            }
            cntVO.getMethodCnt().computeIfAbsent(methodInvokeExpr.getMethodName().toLowerCase(), key -> new AtomicInteger(0)).incrementAndGet();
        }

        if (sqlExpr instanceof SQLAggregateExpr) {
            SQLAggregateExpr aggregateExpr = (SQLAggregateExpr) sqlExpr;
            if (aggregateExpr.isDistinct()) {
                log.warn("聚合函数出现distinct部分:{}", aggregateExpr);
                cntVO.getDistinctCnt().computeIfAbsent(SQLUtils.toSQLString(aggregateExpr), key -> new AtomicInteger(0)).incrementAndGet();
            }
            List<SQLExpr> argList = aggregateExpr.getArguments();
            for (SQLExpr expr : argList) {
                // 重得除了方法外的列循环
                sqlExpr(expr, alias, false, cntVO);
            }
        }
    }

    public static void recuriseTable(SQLTableSource tableSource, CntVO cntVO) {
        if (Objects.isNull(tableSource) || tableSource instanceof SQLLateralViewTableSource) {
            return;
        }

        if (tableSource instanceof SQLExprTableSource) {
            SQLExprTableSource sqlExprTable = (SQLExprTableSource) tableSource;
            sqlExpr(sqlExprTable.getExpr(), sqlExprTable.getAlias(), false, cntVO);
            cntVO.getTableList().add(sqlExprTable);
            return;
        }

        // 子查询
        if (tableSource instanceof SQLSubqueryTableSource) {
            SQLSubqueryTableSource subqueryTable = (SQLSubqueryTableSource) tableSource;
            selectQueryMethod(subqueryTable.getSelect(), cntVO);
            return;
        }

        // UNION
        if (tableSource instanceof SQLUnionQueryTableSource) {
            SQLUnionQueryTableSource sqlUnionQueryTable = (SQLUnionQueryTableSource) tableSource;
            selectQueryMethod(sqlUnionQueryTable.getUnion(), cntVO);
            return;
        }

        SQLJoinTableSource joinTable = (SQLJoinTableSource) tableSource;
        cntVO.getJoinTypeList().add(joinTable.getJoinType());
        sqlExpr(joinTable.getCondition(), joinTable.getAlias(), true, cntVO);
        recuriseTable(joinTable.getLeft(), cntVO);
        recuriseTable(joinTable.getRight(), cntVO);
    }
}
