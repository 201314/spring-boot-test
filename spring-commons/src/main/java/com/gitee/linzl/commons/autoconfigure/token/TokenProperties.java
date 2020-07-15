package com.gitee.linzl.commons.autoconfigure.token;

import io.jsonwebtoken.CompressionCodec;
import io.jsonwebtoken.CompressionCodecs;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Token密钥
 *
 * @author linzhenlie
 * @date 2019/8/28
 */
@Data
@ConfigurationProperties(ignoreUnknownFields = false, prefix = TokenProperties.TOKENPREFIX)
public class TokenProperties {
    public static final String TOKENPREFIX = "token";
    /**
     * 默认不启用Token
     */
    private boolean enable;

    /**
     * 私钥
     */
    private String privateKey;
    /**
     * 公钥
     */
    private String publicKey;
    /**
     * 发行单位
     */
    private String issuer;
    /**
     * 发行时间yyyy-MM-dd
     */
    private String issuedAt;
    /**
     * 主题
     */
    private String subject;
    /**
     * TOKEN 有效时间, 默认一个小时
     */
    private final long DEFAULT_EXPIRE_TIME = 60 * 1000 * 1000;

    private Long expiration = DEFAULT_EXPIRE_TIME;
    /**
     * 如果当前时间在notBefore时间之前，则Token不被接受；一般都会留一些余地，比如几分钟
     */
//	private Date notBefore;

    /**
     * 接收TOKEN的用户
     */
    private String audience;
    /**
     * 一次性TOKEN的id
     */
    private String id;
    /**
     * TOKEN 压缩算法
     */
    private CompressionCodec compress = CompressionCodecs.DEFLATE;
    /**
     * 使用的签名算法
     */
    private SignatureAlgorithm algorithm = SignatureAlgorithm.NONE;

}
