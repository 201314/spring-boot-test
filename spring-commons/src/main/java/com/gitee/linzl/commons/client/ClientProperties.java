package com.gitee.linzl.commons.client;

import lombok.Getter;
import lombok.Setter;

/**
 * @author linzhenlie-jk
 * @date 2021/7/23
 */
@Setter
@Getter
public class ClientProperties {
    /**
     * 服务地址
     */
    private String serverUrl;
    /**
     * 分配的appId
     */
    private String appId;
    /**
     * 公钥
     */
    private String publicKey;
    /**
     * 你自己的RSA(至少1024位)私钥,用于RSA签名
     */
    private String pivateKey;
}
