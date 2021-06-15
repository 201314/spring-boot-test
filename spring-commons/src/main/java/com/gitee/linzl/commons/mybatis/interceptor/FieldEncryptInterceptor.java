package com.gitee.linzl.commons.mybatis.interceptor;

import com.gitee.linzl.commons.tools.EncryptUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.builder.SqlSourceBuilder;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.resultset.ResultSetHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Statement;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;


@Intercepts({
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class,
                RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class}),
        @Signature(type = ResultSetHandler.class, method = "handleResultSets", args = {Statement.class})
})
@SuppressWarnings("unchecked")
@Slf4j
/**
 * 借鉴参考mybatis-cipher，地址git@gitee.com:Jerry.hu/mybatis-cipher.git
 *
 * @author linzhenlie-jk
 * @date 2020/7/22
 */
public class FieldEncryptInterceptor implements Interceptor {
    private static final int MAPPED_STATEMENT_INDEX = 0;
    private static final int PARAMETER_INDEX = 1;

    @Autowired
    private EncryptUtil encryptUtil;


    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object target = invocation.getTarget();
        if (invocation.getTarget() instanceof ResultSetHandler) {
            return invocation.proceed();
        }
        log.info("target:【{}】", target);
        Object[] args = invocation.getArgs();
        Object parameter = args[PARAMETER_INDEX];
        MappedStatement ms = (MappedStatement) args[MAPPED_STATEMENT_INDEX];
        BoundSql boundSql = ms.getBoundSql(parameter);
        log.info("入参parameter:【{}】", ms.getParameterMap());
        log.info("入参boundSql:【{}】", SqlSourceBuilder.removeExtraWhitespaces(boundSql.getSql()));
        SqlCommandType sqlCommandType = ms.getSqlCommandType();
        if (SqlCommandType.INSERT.equals(sqlCommandType) || SqlCommandType.UPDATE.equals(sqlCommandType)) {
            updateParameters(parameter);
        }

        Object object = invocation.proceed();

        // 解密数据
        if (SqlCommandType.SELECT.equals(sqlCommandType)) {
            return crypt(object, false);
        }
        return object;
    }

    /**
     * 修改入参信息
     */
    private void updateParameters(Object parameter) {
        if (parameter instanceof Map) {
            Map<String, Object> map = getParameterMap(parameter);
            map.forEach((k, val) -> {
                crypt(val, true);
            });
        } else {
            crypt(parameter, true);
        }
    }

    /**
     * 获取参数的map 集合
     *
     * @param parameter 参数object
     * @return map 集合
     */
    private Map<String, Object> getParameterMap(Object parameter) {
        Set<Integer> hashCodeSet = new HashSet<>();
        return ((Map<String, Object>) parameter)
                .entrySet().stream()
                .filter(e -> Objects.nonNull(e.getValue()))
                .filter(r -> {
                    if (hashCodeSet.contains(r.getValue().hashCode())) {
                        return false;
                    }
                    hashCodeSet.add(r.getValue().hashCode());
                    return true;
                }).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    /**
     * @param object  需要解密的数据源
     * @param encrypt true 加密，false解密
     * @return
     */
    private Object crypt(Object object, boolean encrypt) {
        if (Objects.isNull(object)) {
            return object;
        }

        if (object instanceof Collection) {
            List<Object> list = (List<Object>) object;
            if (encrypt) {
                encryptUtil.encryptField(list);
            } else {
                encryptUtil.decryptField(list);
            }
            return object;
        }

        if (encrypt) {
            encryptUtil.encryptField(object);
        } else {
            encryptUtil.decryptField(object);
        }
        return object;
    }


    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }
}
