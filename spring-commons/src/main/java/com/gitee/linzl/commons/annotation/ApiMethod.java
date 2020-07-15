package com.gitee.linzl.commons.annotation;

import java.lang.annotation.*;

/** 
 * API方法
 * 
 * @author linzhenlie
 * @date 2019/8/27
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ApiMethod {

	String value() default "";
}
