package com.gitee.linzl.commons.annotation;

import java.lang.annotation.*;

/**
 * 标记在Controller，表示需要有token权限才能访问
 * <p>
 * 因为注解可用于类级别，所以AOP不适合，AOP更适用于方法级别
 *
 * @author linzhenlie
 * @date 2019/8/28
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.TYPE})
@Documented
public @interface RequiredPermissionToken {

}
