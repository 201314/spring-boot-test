package com.gitee.linzl.commons.autoconfigure.token;

import io.jsonwebtoken.*;
import org.springframework.util.Assert;

import java.security.Key;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Objects;

/**
 * @author linzhenlie
 * @date 2019/8/28
 */
public class TokenServiceHandler implements ITokenService {
    private TokenProperties properties;
    /**
     * 公钥
     */
    private Key pubkey;
    /**
     * 私钥
     */
    private Key priKey;

    /**
     * @param properties
     * @param pubKey     公钥，用于验签
     * @param priKey     私钥，用于加签
     */
    public TokenServiceHandler(TokenProperties properties, Key pubKey, Key priKey) {
        this.properties = properties;
        this.pubkey = pubKey;
        this.priKey = priKey;
        Assert.isNull(this.properties, "配置属性不能为空");
        Assert.isNull(this.pubkey, "公钥不能为空");
        Assert.isNull(this.priKey, "私钥不能为空");
    }

    @Override
    public String generate(String extBody) {
        JwtBuilder builder = Jwts.builder();
        if (Objects.nonNull(properties.getIssuer())) {
            builder.setIssuer(properties.getIssuer());
        }
        if (Objects.nonNull(properties.getIssuedAt())) {
            LocalDate localDate = LocalDate.parse(properties.getIssuedAt(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            ZoneId zoneId = ZoneId.systemDefault();
            ZonedDateTime zdt = localDate.atStartOfDay(zoneId);
            builder.setIssuedAt(Date.from(zdt.toInstant()));
        }
        if (Objects.nonNull(properties.getSubject())) {
            builder.setSubject(properties.getSubject());
        }
        if (Objects.nonNull(properties.getExpiration())) {
            builder.setExpiration(new Date(System.currentTimeMillis() + properties.getExpiration()));
        }
        /*if (Objects.nonNull(properties.getNotBefore())) {
            builder.setNotBefore(properties.getNotBefore());
        }*/
        if (Objects.nonNull(properties.getAudience())) {
            builder.setAudience(properties.getAudience());
        }
        if (Objects.nonNull(properties.getId())) {
            builder.setId(properties.getId());
        }

        // 定义额外的属性
        builder.claim("ext", extBody)
                .compressWith(properties.getCompress())
                // 签名算法，及key
                .signWith(properties.getAlgorithm(), this.priKey);
        return builder.compact();
    }

    @Override
    public String parse(String token) {
        JwtParser parser = Jwts.parser();
        if (Objects.nonNull(properties.getIssuer())) {
            parser.requireIssuer(properties.getIssuer());
        }
        if (Objects.nonNull(properties.getIssuedAt())) {
            LocalDate localDate = LocalDate.parse(properties.getIssuedAt(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            ZoneId zoneId = ZoneId.systemDefault();
            ZonedDateTime zdt = localDate.atStartOfDay(zoneId);
            parser.requireIssuedAt(Date.from(zdt.toInstant()));
        }
        if (Objects.nonNull(properties.getSubject())) {
            parser.requireSubject(properties.getSubject());
        }
        /*if (Objects.nonNull(properties.getExpiration())) {
            parser.requireExpiration(properties.getExpiration());
        }
        if (Objects.nonNull(properties.getNotBefore())) {
            parser.requireNotBefore(properties.getNotBefore());
        }*/
        if (Objects.nonNull(properties.getAudience())) {
            parser.requireAudience(properties.getAudience());
        }
        if (Objects.nonNull(properties.getId())) {
            parser.requireId(properties.getId());
        }

        // require表示必须包含该属性
        Jws<Claims> claims = Jwts.parser()
                .setSigningKey(this.pubkey)
                .parseClaimsJws(token);

        // 用户添加的内容，这些需要拿来解析
        return (String) claims.getBody().get("ext");
    }
}
