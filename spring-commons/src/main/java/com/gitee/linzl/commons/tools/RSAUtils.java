package com.gitee.linzl.commons.tools;

import com.gitee.linzl.commons.client.ApiConstant;
import com.gitee.linzl.commons.exception.ApiException;
import org.apache.commons.lang3.StringUtils;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.security.Key;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Objects;

public class RSAUtils {
    //1024位单次加密最大长度 117
    private static final int MAX_ENCRYPT_BLOCK = 117;
    //1024位rsa单次最大解密长度 128
    private static final int MAX_DECRYPT_BLOCK = 128;

    /**
     * 公钥加密
     *
     * @param content
     * @param key
     * @return
     */
    public static String encryptByPublic(String content, String encodedKey) {
        return encryptByPublic(content, encodedKey, null);
    }

    public static String encryptByPublic(String content, String encodedKey, String charset) {
        if (StringUtils.isEmpty(encodedKey)) {
            throw new ApiException("密钥为空");
        }
        if (StringUtils.isEmpty(content)) {
            throw new ApiException("待加密数据为空");
        }
        PublicKey key = null;
        try {
            key = getPublicKeyFromX509(ApiConstant.ENCRYPT_TYPE_RSA, encodedKey);
        } catch (Exception e) {
            throw new ApiException("加密异常", e);
        }
        return encrypt(content, key, charset);
    }

    public static String encryptByPrivate(String content, String encodedKey) {
        return encryptByPrivate(content, encodedKey, null);
    }

    public static String encryptByPrivate(String content, String encodedKey, String charset) {
        if (StringUtils.isEmpty(encodedKey)) {
            throw new ApiException("密钥为空");
        }
        if (StringUtils.isEmpty(content)) {
            throw new ApiException("待加密数据为空");
        }
        PrivateKey key = null;
        try {
            key = getPrivateKeyFromPKCS8(ApiConstant.ENCRYPT_TYPE_RSA, encodedKey);
        } catch (Exception e) {
            throw new ApiException("加密异常", e);
        }
        return encrypt(content, key, charset);
    }

    /**
     * 公钥解密
     *
     * @param content
     * @param key
     * @return
     */
    public static String decryptByPublic(String content, String encodedKey) {
        return decryptByPublic(content, encodedKey, null);
    }

    public static String decryptByPublic(String content, String encodedKey, String charset) {
        if (StringUtils.isEmpty(encodedKey)) {
            throw new ApiException("解密密钥为空");
        }
        if (StringUtils.isEmpty(content)) {
            throw new ApiException("待解密数据为空");
        }
        PublicKey key = null;
        try {
            key = getPublicKeyFromX509(ApiConstant.ENCRYPT_TYPE_RSA, encodedKey);
        } catch (Exception e) {
            throw new ApiException("解密异常", e);
        }
        return decrypt(content, key, charset);
    }

    public static String decryptByPrivate(String content, String encodedKey) {
        return decryptByPrivate(content, encodedKey, null);
    }

    public static String decryptByPrivate(String content, String encodedKey, String charset) {
        if (StringUtils.isEmpty(encodedKey)) {
            throw new ApiException("解密密钥为空");
        }
        if (StringUtils.isEmpty(content)) {
            throw new ApiException("待解密数据为空");
        }
        PrivateKey key = null;
        try {
            key = getPrivateKeyFromPKCS8(ApiConstant.ENCRYPT_TYPE_RSA, encodedKey);
        } catch (Exception e) {
            throw new ApiException("解密异常", e);
        }
        return decrypt(content, key, charset);
    }

    public static PublicKey getPublicKeyFromX509(String algorithm, String encodedKey) throws Exception {
        KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
        byte[] key = Base64.getDecoder().decode(encodedKey);
        return keyFactory.generatePublic(new X509EncodedKeySpec(key));
    }

    public static PrivateKey getPrivateKeyFromPKCS8(String algorithm, String encodedKey) throws Exception {
        KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
        byte[] key = Base64.getDecoder().decode(encodedKey);
        return keyFactory.generatePrivate(new PKCS8EncodedKeySpec(key));
    }

    private static String encrypt(String content, Key encodedKey, String charset) {
        if (Objects.isNull(encodedKey)) {
            throw new ApiException("加密密钥为空");
        }
        if (StringUtils.isEmpty(content)) {
            throw new ApiException("待加密数据为空");
        }
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Cipher cipher = Cipher.getInstance(ApiConstant.ENCRYPT_TYPE_RSA);
            cipher.init(Cipher.ENCRYPT_MODE, encodedKey);
            byte[] data = null;
            if (StringUtils.isBlank(charset)) {
                data = content.getBytes();
            } else {
                data = content.getBytes(charset);
            }
            int inputLen = data.length;

            int offSet = 0;
            byte[] cache;
            int i = 0;
            // 对数据分段加密
            while (inputLen - offSet > 0) {
                if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
                    cache = cipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK);
                } else {
                    cache = cipher.doFinal(data, offSet, inputLen - offSet);
                }
                out.write(cache, 0, cache.length);
                i++;
                offSet = i * MAX_ENCRYPT_BLOCK;
            }
            return Base64.getEncoder().encodeToString(out.toByteArray());
        } catch (Exception e) {
            throw new ApiException("加密异常", e);
        }
    }

    private static String decrypt(String content, Key encodedKey, String charset) {
        if (Objects.isNull(encodedKey)) {
            throw new ApiException("解密密钥为空");
        }
        if (StringUtils.isEmpty(content)) {
            throw new ApiException("待解密数据为空");
        }

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Cipher cipher = Cipher.getInstance(ApiConstant.ENCRYPT_TYPE_RSA);
            cipher.init(Cipher.DECRYPT_MODE, encodedKey);
            byte[] data = Base64.getDecoder().decode(content);
            int inputLen = data.length;
            int offSet = 0;
            byte[] cache;
            int i = 0;
            // 对数据分段解密
            while (inputLen - offSet > 0) {
                if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
                    cache = cipher.doFinal(data, offSet, MAX_DECRYPT_BLOCK);
                } else {
                    cache = cipher.doFinal(data, offSet, inputLen - offSet);
                }
                i++;
                out.write(cache, 0, cache.length);
                offSet = i * MAX_DECRYPT_BLOCK;
            }
            return StringUtils.isBlank(charset) ? new String(out.toByteArray())
                    : new String(out.toByteArray(), charset);
        } catch (Exception e) {
            throw new ApiException("解密处理异常", e);
        }
    }

    public static String rsaSign(String content, String privateKey) throws ApiException {
        return rsaSign(content, privateKey, null);
    }

    public static String rsaSign(String content, String privateKey,
                                 String charset) throws ApiException {
        try {
            PrivateKey priKey = getPrivateKeyFromPKCS8(ApiConstant.ENCRYPT_TYPE_RSA, privateKey);

            java.security.Signature signature = java.security.Signature
                    .getInstance(ApiConstant.SIGN_ALGORITHMS);

            signature.initSign(priKey);

            if (StringUtils.isEmpty(charset)) {
                signature.update(content.getBytes());
            } else {
                signature.update(content.getBytes(charset));
            }

            return Base64.getEncoder().encodeToString(signature.sign());
        } catch (Exception e) {
            throw new ApiException("RSA签名异常", e);
        }
    }

    public static boolean rsaCheckContent(String content, String sign, String publicKey) throws ApiException {
        return rsaCheckContent(content, sign, publicKey, null);
    }

    public static boolean rsaCheckContent(String content, String sign, String publicKey,
                                          String charset) throws ApiException {
        try {
            PublicKey pubKey = getPublicKeyFromX509(ApiConstant.ENCRYPT_TYPE_RSA, publicKey);

            java.security.Signature signature = java.security.Signature
                    .getInstance(ApiConstant.SIGN_ALGORITHMS);

            signature.initVerify(pubKey);

            if (StringUtils.isEmpty(charset)) {
                signature.update(content.getBytes());
            } else {
                signature.update(content.getBytes(charset));
            }

            return signature.verify(Base64.getDecoder().decode(sign.getBytes()));
        } catch (Exception e) {
            throw new ApiException("RSA验签异常", e);
        }
    }

}
