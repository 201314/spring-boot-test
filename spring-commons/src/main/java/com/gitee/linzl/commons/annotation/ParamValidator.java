package com.gitee.linzl.commons.annotation;

import java.lang.annotation.*;

/**
 * 自定义参数校验注解
 *
 * @author linzhenlie
 * @date 2020-05-11
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ParamValidator {
}
