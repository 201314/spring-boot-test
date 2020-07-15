package com.gitee.linzl.commons.mybatis.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 加密注解
 * <p>
 * 按照约定按需增加字段  ***Md5x,***Encrypt
 * <p>
 * 如需要加密的字段:
 * <p>
 * String mobile;
 * <p>
 * 需要查询，则增加字段
 * String mobileMd5x;
 * <p>
 * 需要还原密文,则增加字段
 * String mobileEncrypt;
 *
 * @author linzhenlie
 * @date 2020-06-30
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD})
public @interface Encrypted {

}
