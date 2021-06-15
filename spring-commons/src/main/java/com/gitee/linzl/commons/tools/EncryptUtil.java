package com.gitee.linzl.commons.tools;

import com.gitee.linzl.commons.mybatis.annotation.Encrypted;
import com.gitee.linzl.commons.mybatis.service.CryptService;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ConcurrentReferenceHashMap;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author linzhenlie-jk
 * @date 2021/6/15
 */
public class EncryptUtil {
    private static final Map<Class<?>, List<Field>> encryptFieldsCache = new ConcurrentReferenceHashMap<>(256);
    private static final String MD5X = "Md5x";
    private static final String ENCRYPTX = "Encrypt";

    /**
     * 类型转换
     */
    private ConversionService defaultConversionService = DefaultConversionService.getSharedInstance();
    /**
     * 类型转换
     */
    private ConversionService conversionService;
    /**
     * 加解密服务
     */
    private CryptService cryptService;

    public EncryptUtil(CryptService cryptService) {
        this.cryptService = cryptService;
        this.conversionService = defaultConversionService;
    }

    public EncryptUtil(CryptService cryptService, ConversionService conversionService) {
        this.cryptService = cryptService;
        this.conversionService = conversionService;
    }

    public ConversionService getConversionService() {
        if (Objects.isNull(conversionService)) {
            return defaultConversionService;
        }
        return conversionService;
    }


    public void encryptField(List<Object> list) {
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        list.forEach(entity -> {
            encryptField(entity);
        });
    }

    public void encryptField(Object entity) {
        List<Field> fieldList = fetchEncryptFields(entity);
        fieldList.forEach(field -> {
            String value = String.valueOf(this.getField(field, entity));

            if (StringUtils.isEmpty(value)) {
                return;
            }
            ReflectionUtils.makeAccessible(field);
            // 加密字段的原文置为空
            ReflectionUtils.setField(field, entity, getConversionService().convert(value, field.getType()));
            // ReflectionUtils.setField(field, entity, null);

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


    public void decryptField(List<Object> list) {
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        list.forEach(entity -> {
            decryptField(entity);
        });
    }

    public void decryptField(Object entity) {
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
            ReflectionUtils.setField(field, entity, getConversionService().convert(cryptService.decrypt(value),
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
}
