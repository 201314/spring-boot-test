package com.gitee.linzl.commons.mybatis.interceptor;

import com.gitee.linzl.commons.api.BaseEntity;
import com.gitee.linzl.commons.mybatis.service.OptUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 自动添充公共字段
 *
 * @author linzhenlie
 * @date 2020-06-05
 */
@Intercepts({
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}),
})
@Slf4j
public class AutoFillFieldInterceptor implements Interceptor {
    private static final int MAPPED_STATEMENT_INDEX = 0;
    private static final int PARAMETER_INDEX = 1;

    private OptUserService optUserService;

    public AutoFillFieldInterceptor(OptUserService optUserService) {
        this.optUserService = optUserService;
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object target = invocation.getTarget();
        Object[] args = invocation.getArgs();

        // 序号，对应着@Signature注解中的 args 序号
        Object parameter = args[PARAMETER_INDEX];
        MappedStatement ms = (MappedStatement) args[MAPPED_STATEMENT_INDEX];
        SqlCommandType sqlCommandType = ms.getSqlCommandType();
        log.info("parameter值:【{}】", parameter);

        /**
         * DefaultSqlSession.StrictMap: 不使用@Param注解的情况下
         * MapperMethod.ParamMap: 使用@Param注解的情况下,或者是多参数的情况下
         * {注解@Param的value=***, param1=***}
         * 此时两个***的内容相同,使用@Param注解会有一个注解内指定的名字，还有一个parmaN(N为序号)
         *
         * 其实都是Map
         */
        if (SqlCommandType.INSERT.equals(sqlCommandType) || SqlCommandType.UPDATE.equals(sqlCommandType)) {
            if (parameter instanceof Map) {
                Map map = (Map) parameter;
                Set<Object> sets = new HashSet<>(map.size());
                for (Object o : map.keySet()) {
                    sets.add(map.get(o));
                }

                for (Object value : sets) {
                    process(value, sqlCommandType);
                }
            } else {
                process(parameter, sqlCommandType);
            }
        }
        return invocation.proceed();
    }

    private void process(Object parameter, SqlCommandType sqlCommandType) {
        if (parameter instanceof List) {
            ((List) parameter).stream().forEach(param -> {
                fillField(param, sqlCommandType);
            });
        } else if (parameter instanceof Collection) {
            ((Collection) parameter).stream().forEach(param -> {
                fillField(param, sqlCommandType);
            });
        } else if (parameter.getClass().isArray()) {
            Arrays.asList((Object[]) parameter).stream().forEach(param -> {
                fillField(param, sqlCommandType);
            });
        } else {
            fillField(parameter, sqlCommandType);
        }
    }

    private void fillField(Object param, SqlCommandType sqlCommandType) {
        if (param instanceof BaseEntity) {
            BaseEntity entity = (BaseEntity) param;
            if (SqlCommandType.INSERT.equals(sqlCommandType)) {
                entity.setCreatedBy(optUserService.getUserId());
                entity.setCreatedTime(LocalDateTime.now());
            }
            if (SqlCommandType.INSERT.equals(sqlCommandType) || SqlCommandType.UPDATE.equals(sqlCommandType)) {
                entity.setUpdatedBy(optUserService.getUserId());
                entity.setUpdatedTime(LocalDateTime.now());
            }
        }
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
    }
}
