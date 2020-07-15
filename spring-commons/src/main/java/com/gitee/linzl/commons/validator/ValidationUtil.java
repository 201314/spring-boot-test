package com.gitee.linzl.commons.validator;

import org.hibernate.validator.HibernateValidator;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;

/**
 * 校验工具
 *
 * @author linzhenlie
 * @date 2019/8/27
 */
public class ValidationUtil {
    //    private static Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    private static Validator validator = Validation.byProvider(HibernateValidator.class)
            .configure()
            .failFast(true)
            .buildValidatorFactory()
            .getValidator();

    /**
     * 验证实体
     *
     * @param obj
     * @return java.util.Set<javax.validation.ConstraintViolation < T>>
     */
    public static <T> Set<ConstraintViolation<T>> validate(T obj) {
        Set<ConstraintViolation<T>> set = validator.validate(obj);
        for (ConstraintViolation<T> constraintViolation : set) {
            System.out.println("验证属性:" + constraintViolation.getPropertyPath());
            System.out.println("验证提醒:" + constraintViolation.getMessage());
        }
        return set;
    }

    /**
     * @param
     * @return void
     */
    public static void validateProperty() {

    }

    public static void validateValue() {

    }

}
