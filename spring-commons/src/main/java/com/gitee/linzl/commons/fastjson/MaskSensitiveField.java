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
public @interface MaskSensitiveField {
    /**
     * 保留左边几位
     *
     * @return
     */
    int left() default 0;

    /**
     * 保留右边几位
     *
     * @return
     */
    int right() default 0;

    /**
     * 保留最大长度
     *
     * @return
     */
    int max() default 256;

    /**
     * 脱敏时默认使用*填充
     *
     * @return
     */
    String padding() default "*";

    /**
     * 当使用正则时，left,right配置失效
     *
     * @return
     */
    String regex() default "";
}
