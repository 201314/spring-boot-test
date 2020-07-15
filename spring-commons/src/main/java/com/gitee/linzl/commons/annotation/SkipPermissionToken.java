package com.gitee.linzl.commons.annotation;

import java.lang.annotation.*;

/**
 * 跳过资源访问权限验证
 *
 * @author linzhenlie
 * @date 2019/8/28
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.METHOD})
@Documented
public @interface SkipPermissionToken {

}
