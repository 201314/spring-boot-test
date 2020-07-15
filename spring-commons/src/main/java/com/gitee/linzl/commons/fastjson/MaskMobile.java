package com.gitee.linzl.commons.fastjson;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author linzhenlie
 * @date 2019/10/8
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MaskMobile {
    /**
     * 保留左边几位
     *
     * @return
     */
    int left() default 3;

    /**
     * 保留右边几位
     *
     * @return
     */
    int right() default 4;

    /**
     * 脱敏时默认使用*填充
     *
     * @return
     */
    String padding() default "*";
}
