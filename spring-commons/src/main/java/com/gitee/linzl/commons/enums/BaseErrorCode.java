package com.gitee.linzl.commons.enums;

import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Objects;

/**
 * 参考https://docs.open.alipay.com/common/105806
 * <p>
 * 10000以内是公共参数
 *
 * @author linzl
 * @description
 * @email 2225010489@qq.com
 * @date 2018年11月9日
 */
public enum BaseErrorCode implements IBaseErrorCode {
    // 成功
    SUCCESS("0000", "操作成功", "success"),
    // 系统异常
    SYS_ERROR("0001", "系统异常", "unknown error"),
    // 服务不可用
    SERVICE_NOT_AVAILABLE("2000", "服务不可用", "service not available"),
    // 非法请求
    NOT_FOUND_URL("2001", "非法请求", "not found url"),
    // 非法操作,进行黑客入侵方式的尝试
    ILLEGAL_OPERATION("2002", "非法操作", "illegal operation"),

    BAD_REQUEST("2003", "非法请求", "Bad request"),
    UNSUPPORTED_MEDIA_TYPE("2004", "不支持的媒体类型", "Http status 415, Unsupported Media Type!"),

    // 缺少参数
    MISSING_PARAMETERS("4001", "缺少参数", "missing parameters"),
    // 无效参数
    INVALID_PARAMETERS("4002", "无效参数", "invalid parameters"),

    // 业务处理失败
    BUSINESS_PROCESSING_FAIL("4004", "业务处理失败", "business processing fail"),
    // 权限不足
    NOT_ENOUGH_AUTHORITY("4006", "权限不足", "not enough authority");

    /**
     * 数字错误码
     */
    private final String code;
    /**
     * 中文
     */
    private final String msg;
    /**
     * 英文
     */
    private final String enMsg;

    BaseErrorCode(final String code, final String msg, final String enMsg) {
        this.code = code;
        this.msg = msg;
        this.enMsg = enMsg;
    }

    public static IBaseErrorCode fromCode(String reqCode) {
        if (StringUtils.isEmpty(reqCode)) {
            return BaseErrorCode.SYS_ERROR;
        }
        IBaseErrorCode filerCode =
                Arrays.stream(AccountErrorCode.values()).filter(accountErrorCode -> Objects.equals(accountErrorCode.getCode(), reqCode)).findFirst().orElse(null);
        return filerCode;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMsg() {
        return msg;
    }

    @Override
    public String getEnMsg() {
        return enMsg;
    }

    @Override
    public String toString() {
        return String.format(" ErrorCode:{code=%s, msg=%s} ", code, msg);
    }
}
