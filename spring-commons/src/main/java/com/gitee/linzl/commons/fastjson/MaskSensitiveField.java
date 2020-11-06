package com.gitee.linzl.commons.fastjson;

import java.lang.annotation.*;

/**
 * @author linzhenlie
 * @date 2019/10/8
 */
@Documented
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface MaskSensitiveField {
    /**
     * 保留左边几位,必须是正整数
     *
     * @return
     */
    int left() default 0;

    /**
     * 保留右边几位,必须是正整数
     *
     * @return
     */
    int right() default 0;

    /**
     * 保留最大长度,必须是正整数
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
