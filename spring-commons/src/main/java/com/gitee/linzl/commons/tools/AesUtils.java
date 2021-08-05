package com.gitee.linzl.commons.tools;

import com.gitee.linzl.commons.exception.ApiException;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class AesUtils {
    public static String doEncrypt(String secret, String content, String charset) throws ApiException {
        try {
            SecretKeySpec key = new SecretKeySpec(secret.getBytes(charset), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] result = cipher.doFinal(content.getBytes(charset));
            return Base64.encodeBase64String(result);
        } catch (Exception e) {
            throw new ApiException("AES加密异常", e);
        }
    }

    public static String doDecrypt(String secret, String content, String charset) throws ApiException {
        try {
            SecretKeySpec key = new SecretKeySpec(secret.getBytes(charset), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] result = cipher.doFinal(Base64.decodeBase64(content.getBytes(charset)));
            return new String(result, charset);
        } catch (Exception e) {
            throw new ApiException("AES解密异常", e);
        }
    }
}
