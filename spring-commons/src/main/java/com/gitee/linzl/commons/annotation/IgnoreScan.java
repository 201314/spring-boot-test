package com.gitee.linzl.commons.annotation;

import java.lang.annotation.*;

/**
 * 忽略扫描
 *
 * @author linzhenlie
 * @date 2019/8/29
 */
@Target({ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface IgnoreScan {
}
