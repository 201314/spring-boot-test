package com.gitee.linzl.commons.config;

import com.gitee.linzl.commons.mybatis.interceptor.AutoFillFieldInterceptor;
import com.gitee.linzl.commons.mybatis.interceptor.FieldEncryptInterceptor;
import com.gitee.linzl.commons.mybatis.service.CryptService;
import com.gitee.linzl.commons.mybatis.service.OptUserService;
import com.gitee.linzl.commons.tools.EncryptUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.LocalCacheScope;
import org.mybatis.spring.boot.autoconfigure.ConfigurationCustomizer;
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
public class MybatisConfiguration {
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

    @ConditionalOnClass(value = {ConfigurationCustomizer.class})
    @ConditionalOnMissingBean
    @Bean
    public ConfigurationCustomizer mybatisCustomizer() {
        log.info("mybatis关闭一级Session、二级Map缓存并【驼峰命名法】转换字段");
        ConfigurationCustomizer configurationCustomizer = new ConfigurationCustomizer() {
            @Override
            public void customize(Configuration configuration) {
                // Session一级缓存【关闭】，只要命中索引，查询很快
                configuration.setLocalCacheScope(LocalCacheScope.STATEMENT);
                // 关闭二级缓存，缓存由一个Map保存，容量有限且没有过期管理
                configuration.setCacheEnabled(Boolean.FALSE);
                // 使用驼峰命名法转换字段,此时数据库可以直接映射到Entity字段
                configuration.setMapUnderscoreToCamelCase(Boolean.TRUE);
            }
        };
        return configurationCustomizer;
    }
}
