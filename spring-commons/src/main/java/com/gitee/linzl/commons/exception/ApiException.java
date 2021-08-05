package com.gitee.linzl.commons.exception;

/**
 * @author linzhenlie-jk
 * @date 2021/6/28
 */
public class ApiException extends RuntimeException {
    private String code;
    private String msg;

    public ApiException() {
        super();
    }

    public ApiException(String message, Throwable cause) {
        super(message, cause);
    }

    public ApiException(String message) {
        super(message);
    }

    public ApiException(Throwable cause) {
        super(cause);
    }

    public ApiException(String errCode, String errMsg) {
        super(errCode + ":" + errMsg);
        this.code = errCode;
        this.msg = errMsg;
    }

    public String getCode() {
        return this.code;
    }

    public String getMsg() {
        return this.msg;
    }
}
