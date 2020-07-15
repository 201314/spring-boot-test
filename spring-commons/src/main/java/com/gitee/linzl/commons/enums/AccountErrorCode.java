package com.gitee.linzl.commons.enums;


import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

/**
 * 10000开始
 *
 * @author linzhenlie
 * @date 2019/9/4
 */
public enum AccountErrorCode implements IBaseErrorCode {

    ACCOUNT_HAD_EXIST("10000", "账号已存在", "account had exist"),

    ACCOUNT_OR_PWD_ERROR("10001", "账号或密码错误", "account or password is error");

    /**
     * 数字错误码
     */
    private final String code;
    /**
     * 英文
     */
    private final String msg;
    /**
     * 英文
     */
    private final String enMsg;

    AccountErrorCode(final String code, final String msg, final String enMsg) {
        this.code = code;
        this.msg = msg;
        this.enMsg = enMsg;
    }

    public static IBaseErrorCode fromCode(String reqCode) {
        if(StringUtils.isEmpty(reqCode)){
            return BaseErrorCode.SYS_ERROR;
        }
        IBaseErrorCode filerCode =
                Arrays.stream(AccountErrorCode.values()).filter(accountErrorCode -> Objects.equals(accountErrorCode.getCode(),reqCode)).findFirst().orElse(null);
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
