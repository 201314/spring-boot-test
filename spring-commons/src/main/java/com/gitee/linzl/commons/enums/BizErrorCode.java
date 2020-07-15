package com.gitee.linzl.commons.enums;

import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

/**
 * 参考https://docs.open.alipay.com/common/105806
 *
 * @author linzl
 * @description
 * @email 2225010489@qq.com
 * @date 2018年11月9日
 */
public enum BizErrorCode {
    MD5_CHECK_FAIl("1000", "md5校验失败");

    // 由业务模块返回
    // 业务返回码:ACQ.TRADE_HAS_SUCCESS
    private final String subCode;
    // 业务返回码描述:交易已被支付
    private final String subMsg;

    BizErrorCode(final String subCode, final String subMsg) {
        this.subCode = subCode;
        this.subMsg = subMsg;
    }

    public static BizErrorCode fromCode(String reqCode) {
        if(StringUtils.isEmpty(reqCode)){
            return null;
        }
        BizErrorCode filerCode =
                Arrays.stream(BizErrorCode.values()).filter(bizErrorCode -> Objects.equals(bizErrorCode.getSubCode(),
                        reqCode)).findFirst().orElse(null);
        return Optional.ofNullable(filerCode).orElse(null);
    }

    public String getSubCode() {
        return subCode;
    }

    public String getSubMsg() {
        return subMsg;
    }

    @Override
    public String toString() {
        return String.format(" ErrorCode:{code=%s, msg=%s} ", subCode, subMsg);
    }
}
