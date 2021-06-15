package com.gitee.linzl.commons.config;

import com.gitee.linzl.commons.mybatis.interceptor.AutoFillFieldInterceptor;
import com.gitee.linzl.commons.mybatis.interceptor.FieldEncryptInterceptor;
import com.gitee.linzl.commons.mybatis.service.CryptService;
import com.gitee.linzl.commons.mybatis.service.OptUserService;
import com.gitee.linzl.commons.tools.EncryptUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

/**
 * @author linzhenlie
 * @date 2020-06-05
 */
@ConditionalOnClass(value = Mapper.class)
@Component
@Slf4j
public class MybatisConfig {
    @ConditionalOnMissingBean(value = {OptUserService.class})
    @Bean
    public OptUserService defaultOptUserService() {
        return () -> "sys";
    }

    @ConditionalOnMissingBean(value = {CryptService.class})
    @Bean
    public CryptService defaultCryptService() {
        return new CryptService() {
            @Override
            public String md5x(String value) {
                return DigestUtils.md5DigestAsHex(value.getBytes());
            }

            @Override
            public String encrypt(String value) {
                return value;
            }

            @Override
            public String decrypt(String value) {
                return value;
            }
        };
    }

    @Bean
    public AutoFillFieldInterceptor autoFillInterceptor(@Autowired OptUserService optUserService) {
        return new AutoFillFieldInterceptor(optUserService);
    }

    @Bean
    public FieldEncryptInterceptor fieldEncryptInterceptor() {
        return new FieldEncryptInterceptor();
    }

    @Bean
    public EncryptUtil encryptUtil(@Autowired CryptService cryptService) {
        return new EncryptUtil(cryptService);
    }
}
