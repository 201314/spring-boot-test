package com.gitee.linzl.commons.gateway;



import java.io.Serializable;

import javax.validation.constraints.NotBlank;

import com.gitee.linzl.commons.api.BaseRequestProtocol;

import lombok.Getter;
import lombok.Setter;
 
@Setter
@Getter
public class GatewayRequest extends BaseRequestProtocol implements Serializable {
    private static final long serialVersionUID = -8420066159879965843L;
    private String subChannel;
    private String activityInfo;
    private String h5Version;
    private String requestIp;
    private String phoneNo;
 // APP版本号
    private String appVersion;
    // 客户端渠道编号
    @NotBlank
    private String channelSource;
    // 渠道类型:APK/SDK/H5
    @NotBlank
    private String sourceType;
    // 宿主APP编码
    @NotBlank
    private String hostApp;
    // 产品编码
    private String productCode;
    // 用户号
    private String userNo;
    // 客户号
    private String custNo;
    // 授权token
    private String token;
    // 设备信息
    private String deviceInfo;
}
