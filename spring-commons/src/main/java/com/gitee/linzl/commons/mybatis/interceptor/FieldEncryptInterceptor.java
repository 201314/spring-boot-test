package com.gitee.linzl.commons.mybatis.interceptor;

import com.gitee.linzl.commons.mybatis.annotation.Encrypted;
import com.gitee.linzl.commons.mybatis.service.CryptService;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.resultset.ResultSetHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.util.ConcurrentReferenceHashMap;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.sql.Statement;
import java.util.*;
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

    private static final Map<Class<?>, List<Field>> encryptFieldsCache = new ConcurrentReferenceHashMap<>(256);

    private static final String MD5X = "Md5x";
    private static final String ENCRYPTX = "Encrypt";
    /**
     * 加解密服务
     */
    private CryptService cryptService;
    /**
     * 类型转换
     */
    private ConversionService conversionService;

    public FieldEncryptInterceptor(ConversionService conversionService, CryptService cryptService) {
        this.conversionService = conversionService;
        if (Objects.isNull(this.conversionService)) {
            this.conversionService = DefaultConversionService.getSharedInstance();
        }
        this.cryptService = cryptService;
    }

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
        log.info("入参:【{}】", parameter);
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

        List<Object> list;
        if (object instanceof Collection) {
            list = (List<Object>) object;
            list.forEach(entity -> {
                if (encrypt) {
                    encryptField(entity);
                } else {
                    decryptField(entity);
                }
            });
            return object;
        }

        if (encrypt) {
            encryptField(object);
        } else {
            decryptField(object);
        }
        return object;
    }

    private void encryptField(Object entity) {
        List<Field> fieldList = fetchEncryptFields(entity);
        fieldList.forEach(field -> {
            String value = String.valueOf(this.getField(field, entity));

            if (StringUtils.isEmpty(value)) {
                return;
            }
            ReflectionUtils.makeAccessible(field);
            ReflectionUtils.setField(field, entity, conversionService.convert(value, field.getType()));

            Field encryptField = ReflectionUtils.findField(entity.getClass(), field.getName() + ENCRYPTX);
            if (Objects.nonNull(encryptField)) {
                ReflectionUtils.makeAccessible(encryptField);
                ReflectionUtils.setField(encryptField, entity, this.cryptService.encrypt(value));
            }

            Field md5xField = ReflectionUtils.findField(entity.getClass(), field.getName() + MD5X);
            if (Objects.nonNull(md5xField)) {
                ReflectionUtils.makeAccessible(md5xField);
                ReflectionUtils.setField(md5xField, entity, this.cryptService.md5x(value));
            }
        });
    }

    private void decryptField(Object entity) {
        List<Field> fieldList = fetchEncryptFields(entity);
        // 获取获取加密字段、md5x字段、encrypt字段及其对应的值
        fieldList.forEach(field -> {
            Field encryptField = ReflectionUtils.findField(entity.getClass(), field.getName() + ENCRYPTX);
            if (Objects.isNull(encryptField)) {
                return;
            }
            String value = String.valueOf(this.getField(encryptField, entity));
            if (StringUtils.isEmpty(value)) {
                return;
            }

            ReflectionUtils.makeAccessible(field);
            ReflectionUtils.setField(field, entity, conversionService.convert(cryptService.decrypt(value),
                    field.getType()));
        });
    }

    private List<Field> fetchEncryptFields(Object entity) {
        List<Field> fieldList = encryptFieldsCache.get(entity.getClass());
        if (Objects.isNull(fieldList)) {
            fieldList = getFields(entity)
                    .stream()
                    .filter(field -> field.isAnnotationPresent(Encrypted.class))
                    .collect(Collectors.toList());
            encryptFieldsCache.put(entity.getClass(), fieldList);
        }
        return fieldList;
    }

    private List<Field> getFields(Object obj) {
        List<Field> fieldList = new ArrayList<>();
        Class tempClass = obj.getClass();
        //当父类为null的时候说明到达了最上层的父类(Object类).
        while (Objects.nonNull(tempClass)) {
            ReflectionUtils.doWithLocalFields(tempClass, field -> fieldList.add(field));
            //得到父类,然后赋给自己
            tempClass = tempClass.getSuperclass();
        }
        return fieldList;
    }

    /**
     * 获取字段
     *
     * @param
     * @return
     */
    private Object getField(Field field, Object obj) {
        ReflectionUtils.makeAccessible(field);
        return ReflectionUtils.getField(field, obj);
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }
}
