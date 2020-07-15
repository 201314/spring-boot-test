package com.gitee.linzl.commons.autoconfigure.token;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * @author linzhenlie
 * @date 2019/9/2
 */
@Configuration
@EnableConfigurationProperties({TokenProperties.class})
@Slf4j
public class TokenAutoConfiguration {
    @Autowired
    private TokenProperties properties;

    @Bean
    @ConditionalOnProperty(prefix = TokenProperties.TOKENPREFIX, name = "enable", havingValue = "true")
    @ConditionalOnMissingBean(ITokenService.class)
    public ITokenService tokenService() throws NoSuchAlgorithmException, InvalidKeySpecException {
        log.debug("TokenAutoConfiguration初始化=====ITokenService");

        KeyFactory kf = KeyFactory.getInstance("RSA");

        EncodedKeySpec spec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(properties.getPrivateKey()));
        Key privateKey = kf.generatePrivate(spec);

        spec = new X509EncodedKeySpec(Base64.getDecoder().decode(properties.getPublicKey()));
        Key publicKey = kf.generatePublic(spec);
        return new TokenServiceImpl(properties, publicKey, privateKey);
    }
}
