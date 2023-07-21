package com.baomidou.springboot.sql;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.ast.statement.SQLJoinTableSource;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * @author linzhenlie-jk
 * @date 2023/6/5
 */

@Getter
@Slf4j
public class CntVO {
    List<SQLExprTableSource> tableList = new LinkedList<>();

    List<SQLExpr> conditionList = new LinkedList<>();

    List<SQLJoinTableSource.JoinType> joinTypeList = new LinkedList<>();

    private Map<String, AtomicInteger> distinctCnt = new HashMap<>(20);

    private Map<String, AtomicInteger> methodCnt = new HashMap<>(20);

    private AtomicInteger constantCnt = new AtomicInteger(0);

    private Map<String, AtomicInteger> ownerCnt = new HashMap<>(20);

    public void check() {
        /*
        AtomicInteger getJsonObjectCnt = methodCnt.getOrDefault("get_json_object", new AtomicInteger(0));
        if (getJsonObjectCnt.get() > 1) {
            log.error("get_json_object函数出现多次,建议改用json_purple");
        }

        // 只有select有表
        if (CollectionUtils.isEmpty(tableList)) {
            return;
        }
        SQLExprTableSource mainTable = tableList.get(0);
        Map<SQLJoinTableSource.JoinType, Long> collect = joinTypeList.stream().collect(Collectors.groupingBy(joinType -> joinType, Collectors.counting()));

        AtomicInteger mainTableJoinCnt = ownerCnt.getOrDefault(mainTable.getAlias(), new AtomicInteger(0));
        if (mainTableJoinCnt.get() != joinTypeList.size()) {
            log.error("JOIN过程中主表别名:" + mainTable.getAlias() + "出现的次数:" + mainTableJoinCnt.get() + ",少于JOIN次数:" + joinTypeList.size() + ",有数据倾斜风险");
        }

        if (joinTypeList.size() > 10) {
            log.error("每个select出现JOIN的次数不能超过10次");
        }

        if (collect.containsKey(SQLJoinTableSource.JoinType.RIGHT_OUTER_JOIN)) {
            log.error("right join改为使用left join更符合阅读及使用习惯");
        }
        log.info("关联条件:{}", conditionList);*/
        tableList.forEach(sqlExprTable -> {
            if (StringUtils.containsAnyIgnoreCase(sqlExprTable.getSchema(), "fin_", "dwd_tmp")) {
                return;
            }
            log.info("库名:{},表名:{},别名:{}", sqlExprTable.getSchema(), sqlExprTable.getTableName(), sqlExprTable.getAlias());
        });
    }

    public void clean() {
        tableList = new LinkedList<>();
        conditionList = new LinkedList<>();
        joinTypeList = new LinkedList<>();
        distinctCnt = new HashMap<>(20);
        methodCnt = new HashMap<>(20);
        constantCnt = new AtomicInteger(0);
        ownerCnt = new HashMap<>(20);
    }
}
