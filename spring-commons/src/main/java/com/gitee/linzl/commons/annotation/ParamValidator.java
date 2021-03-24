package com.gitee.linzl.commons.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义参数校验注解,是@Valid、BindingResult 的合并版本，用一个注解代替
 *
 * @author linzhenlie
 * @date 2020-05-11
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ParamValidator {
}
