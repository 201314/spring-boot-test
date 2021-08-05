package com.gitee.linzl.commons.cipher;


import com.gitee.linzl.commons.client.ApiConstant;
import com.gitee.linzl.commons.client.ClientProperties;
import com.gitee.linzl.commons.exception.ApiException;
import com.gitee.linzl.commons.tools.RSAUtils;
import com.gitee.linzl.commons.tools.SignUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.TreeMap;

/**
 * @author linzhenlie-jk
 * @date 2021/6/28
 */
public class DefaultCipher implements ICipher {
    private final ClientProperties properties;

    public DefaultCipher(ClientProperties properties) {
        this.properties = properties;
    }

    @Override
    public String encrypt(String rawContent, String signType, String charset) {
        if (!ApiConstant.SIGN_TYPE_RSA.equals(signType)) {
            throw new ApiException("暂不支持该加密方式:" + signType);
        }
        String encryptResult = null;
        try {
            // 采用合作方私钥加密、签名
            encryptResult = RSAUtils.encryptByPrivate(rawContent, properties.getPivateKey(), charset);
        } catch (ApiException e) {
            throw e;
        }
        return encryptResult;
    }

    @Override
    public boolean verify(Map<String, Object> map, String signType) {
        if (!ApiConstant.SIGN_TYPE_RSA.equals(signType)) {
            throw new ApiException("暂不支持该签名方式:" + signType);
        }

        Map<String, Object> sortedParams = new TreeMap<>(map);
        String respSign = (String) sortedParams.get(ApiConstant.SIGN);
        if (StringUtils.isBlank(respSign)) {
            throw new ApiException("响应没有签名");
        }

        sortedParams.put(ApiConstant.APP_ID, properties.getAppId());
        return RSAUtils.rsaCheckContent(SignUtil.createSign(sortedParams), respSign, properties.getPublicKey());
    }

    @Override
    public String sign(Map<String, Object> map, String signType) {
        if (!ApiConstant.SIGN_TYPE_RSA.equals(signType)) {
            throw new ApiException("暂不支持该签名方式:" + signType);
        }

        Map<String, Object> sortedParams = new TreeMap<>(map);
        sortedParams.put(ApiConstant.APP_ID, properties.getAppId());
        return RSAUtils.rsaSign(SignUtil.createSign(sortedParams), properties.getPivateKey());
    }
}
