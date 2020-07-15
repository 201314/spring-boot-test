package com.gitee.linzl.commons.mybatis.service;

/**
 * 加解密服务接口
 */
public interface CryptService {
    String md5x(String value);

    /**
     * 单条加密
     *
     * @param value 待加密字段
     * @return 加密后的字符串
     */
    String encrypt(String value);

    /**
     * 单条解密
     *
     * @param value 待解密字段
     * @return 解密后的字符串
     */
    String decrypt(String value);

}
