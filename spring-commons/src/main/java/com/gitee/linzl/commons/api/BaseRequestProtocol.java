package com.gitee.linzl.commons.api;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 公共基础API
 *
 * @author linzhenlie
 * @date 2019/8/26
 */
@Setter
@Getter
public class BaseRequestProtocol implements Serializable {
    private static final long serialVersionUID = 8438660582714922587L;
    @NotBlank
    private String appId;
    /**
     * 协议版本
     */
    @NotBlank
    private String version;
    /**
     * 随机数
     */
    private String nonceStr;
    /**
     * 时间戳
     */
    @NotNull
    private Long timestamp;
    /**
     * 调用哪个方法
     */
    @NotBlank
    private String method;
    /**
     * 签名
     */
    private String sign;
    /**
     * 签名方式
     */
    private String signType;
    /**
     * 请求字符编码
     */
    private String charset;
    /**
     * 接口业务请求参数
     */
    @NotBlank
    private String bizContent;

    /**
     * 对称加密类型
     *
     * @return
     */
    private String encryptType;
    /**
     * 对称密钥
     */
    private String encryptKey;

    /**
     * 仅允许请求时间与当前系统时间五分钟内的请求
     *
     * @return
     */
    public boolean validateTime() {
        long now = System.currentTimeMillis();
        long subTime = now - timestamp;
        return Math.abs(subTime) <= 5 * 60 * 1000;
    }
}
