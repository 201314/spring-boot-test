package com.gitee.linzl.commons.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * 标记敏感字符注解
 *
 * @author linzhenlie
 * @date 2019/8/27
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = SensitiveWordValidator.class)
public @interface SensitiveWord {
    /**
     * 默认错误消息
     */
    String message() default "包含敏感字符";

    /**
     * 分组
     */
    Class<?>[] groups() default {};

    /**
     * 负载
     */
    Class<? extends Payload>[] payload() default {};

    /**
     * 指定多个时使用
     */
    @Target({ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @interface List {
        SensitiveWord[] value();
    }
}
