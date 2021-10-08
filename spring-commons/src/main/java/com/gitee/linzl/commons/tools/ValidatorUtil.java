package com.gitee.linzl.commons.tools;

import org.springframework.util.CollectionUtils;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.groups.Default;
import java.lang.reflect.Field;
import java.util.*;

/**
 * bean验证器
 *
 * @author linzhenlie
 * @date 2020-05-12
 */
public class ValidatorUtil {
    private static Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    public static <T> Map<String, String> validate(T obj) {
        return validate(obj, Default.class);
    }

    /**
     * 验证并以Map方式返回错误信息
     *
     * @param t
     * @param groups
     * @param <T>
     * @return
     */
    public static <T> Map<String, String> validate(T t, Class... groups) {
        Set<ConstraintViolation<T>> validateResult = validator.validate(t, groups);
        if (CollectionUtils.isEmpty(validateResult)) {
            return Collections.emptyMap();
        }

        Map<String, String> errors = new LinkedHashMap();
        Iterator it = validateResult.iterator();
        while (it.hasNext()) {
            ConstraintViolation violation = (ConstraintViolation) it.next();
            errors.put(violation.getPropertyPath().toString(), violation.getMessage());
        }
        return errors;
    }

    /**
     * 验证集合
     *
     * @param collection
     * @return
     */
    public static <T> Map<String, String> validateList(Collection<T> collection) {
        if (CollectionUtils.isEmpty(collection)) {
            return Collections.emptyMap();
        }

        Map<String, String> errors = null;
        for (Object coll : collection) {
            errors = validate(coll);
            if (!CollectionUtils.isEmpty(errors)) {
                break;
            }
        }
        return errors;
    }

    /**
     * 递归校验参数，复杂对象中的List
     *
     * @param t
     * @param <T>
     * @return
     * @throws IllegalAccessException
     */
    public static <T> Map<String, String> recursionValidate(T t) throws IllegalAccessException {
        Map<String, String> errors = validate(t);

        Field[] fields = t.getClass().getDeclaredFields();
        for (Field field : fields) {
            Class clz = field.getType();
            if (clz.isAssignableFrom(List.class)) {
                Object srcValue = field.get(t);
                errors.putAll(validateList((List) srcValue));
            }
        }
        return errors;
    }
}