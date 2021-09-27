package com.gitee.linzl.log.mask.fastjson;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * @author linzhenlie
 * @date 2019/10/8
 */
@Documented
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@MaskSensitiveField
public @interface MaskBankCard {
    @AliasFor(annotation = MaskSensitiveField.class)
    int left() default 0;

    @AliasFor(annotation = MaskSensitiveField.class)
    int right() default 4;

    @AliasFor(annotation = MaskSensitiveField.class)
    String padding() default "*";
}
