package com.gitee.linzl.commons.client;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gitee.linzl.commons.api.ApiResult;
import com.gitee.linzl.commons.api.BaseApiRequest;
import com.gitee.linzl.commons.api.BaseApiResponse;
import com.gitee.linzl.commons.api.BaseRequestProtocol;
import com.gitee.linzl.commons.cipher.DefaultCipher;
import com.gitee.linzl.commons.cipher.ICipher;
import com.gitee.linzl.commons.exception.ApiException;
import com.gitee.linzl.commons.tools.AesUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * @author linzhenlie-jk
 * @date 2021/6/28
 */
@Slf4j
public class DefaultClient {
    private final String signType = ApiConstant.SIGN_TYPE_RSA;
    private final String encryptType = ApiConstant.ENCRYPT_TYPE_AES;
    private String charset;

    private final ClientProperties properties;
    private final ICipher cipher;
    private final IHttpClient client;

    /**
     * @param serverUrl
     * @param appId
     * @param yourPrivateKey   你自己的私钥，用于RSA签名
     * @param qihooPublicKey   360公钥
     * @param qihooSignSaltKey 签名盐值
     */
    public DefaultClient(ClientProperties properties) {
        this.properties = properties;
        this.cipher = new DefaultCipher(properties);
        this.client = new DefaultHttpClient();
    }

    public <T extends BaseApiResponse> ApiResult<T> execute(BaseApiRequest<T> request) throws ApiException {
        return doExecute(request);
    }

    private <T extends BaseApiResponse> ApiResult<T> doExecute(BaseApiRequest<T> request) throws ApiException {
        // 1.加密
        String bizContent = JSON.toJSONString(request);
        if (log.isDebugEnabled()) {
            log.debug("apiMethod:【{}】,业务参数:【{}】", request.getMethod(), bizContent);
        }

        BaseRequestProtocol requestProtocol = new BaseRequestProtocol();
        requestProtocol.setAppId(this.properties.getAppId());
        requestProtocol.setNonceStr(RandomStringUtils.randomAlphanumeric(32));
        requestProtocol.setTimestamp(System.currentTimeMillis());
        requestProtocol.setMethod(request.getMethod());

        String encrpytKey = null;
        if (request.isNeedEncrypt()) {
            encrpytKey = RandomStringUtils.randomNumeric(16);
            String encryptedBizContent = AesUtils.doEncrypt(encrpytKey, bizContent, getCharset());
            requestProtocol.setEncryptType(this.encryptType);
            requestProtocol.setEncryptKey(this.cipher.encrypt(encrpytKey, this.signType, getCharset()));
            requestProtocol.setBizContent(encryptedBizContent);
        } else {
            requestProtocol.setBizContent(bizContent);
        }
        requestProtocol.setSignType(this.signType);

        String protocol = JSON.toJSONString(requestProtocol);
        Map<String, Object> mustProtocolMap = JSONObject.parseObject(protocol).getInnerMap();

        // 2.签名
        mustProtocolMap.put(ApiConstant.SIGN, this.cipher.sign(mustProtocolMap, this.signType));
        if (log.isDebugEnabled()) {
            log.debug("apiMethod:【{}】,加签后参数:【{}】", request.getMethod(), mustProtocolMap);
        }

        Long start = System.currentTimeMillis();

        // 3.请求
        String result = getTpayHttpClient().post(properties.getServerUrl() + request.getUrl(), JSON.toJSONString(mustProtocolMap),
                request.getMethod());
        if (log.isDebugEnabled()) {
            log.debug("apiMethod:【{}】,耗时:【{}】,响应结果:【{}】", request.getMethod(), System.currentTimeMillis() - start, result);
        }

        // 4.响应验签
        JSONObject jo = JSONObject.parseObject(result);
        ApiResult<String> resp = jo.toJavaObject(ApiResult.class);

        ApiResult<T> response = ApiResult.of();
        response.setStatus(resp.isStatus());
        response.setCode(resp.getCode());
        response.setMsg(resp.getMsg());
        if (resp.isSuccess()) {
            // 成功才需要验签
            Map<String, Object> map = jo.getInnerMap();
            cipher.verify(map, this.signType);
            String realResp = resp.getData();
            // 5.解密,部分接口只返回成功标识
            if (request.isNeedEncrypt() && StringUtils.isNotBlank(realResp)) {
                realResp = AesUtils.doDecrypt(encrpytKey, realResp, getCharset());
            }

            // 5.解密,部分接口只返回成功标识
            if (StringUtils.isNotBlank(realResp)) {
                ParameterizedType parameterizedType = (ParameterizedType) request.getClass().getGenericSuperclass();
                Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                Class<T> respCls = (Class) actualTypeArguments[0];
                T t = (T) JSON.parseObject(realResp, respCls);
                response.setData(t);
            }
        }

        if (log.isDebugEnabled()) {
            log.debug("请求方法:【{}】,解密后响应数据:【{}】", request.getMethod(), JSON.toJSONString(response));
        }
        return response;
    }

    public IHttpClient getTpayHttpClient() {
        return this.client;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public String getCharset() {
        return StringUtils.defaultIfBlank(this.charset, StandardCharsets.UTF_8.name());
    }
}
