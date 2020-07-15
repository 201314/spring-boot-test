package com.gitee.linzl.commons.annotation;

import java.lang.annotation.*;

/**
 * 在需要记录执行耗时的方法上使用
 * 
 * @description
 * @author linzl
 * @email 2225010489@qq.com
 * @date 2019年3月12日
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.METHOD, ElementType.TYPE})
@Documented
public @interface Performance {

}
