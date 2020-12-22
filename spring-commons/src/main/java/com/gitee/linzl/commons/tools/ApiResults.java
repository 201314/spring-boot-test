package com.gitee.linzl.commons.tools;

import com.gitee.linzl.commons.api.ApiResult;
import com.gitee.linzl.commons.enums.BaseErrorCode;
import com.gitee.linzl.commons.enums.IBaseErrorCode;
import com.gitee.linzl.commons.exception.BaseException;

/**
 * @author linzhenlie-jk
 * @date 2020/12/22
 */
public class ApiResults {
    public static <T> ApiResult<T> success() {
        return success(BaseErrorCode.SUCCESS, null);
    }

    public static <T> ApiResult<T> success(T data) {
        return success(BaseErrorCode.SUCCESS, data);
    }

    public static <T> ApiResult<T> success(IBaseErrorCode base, T data) {
        ApiResult api = ApiResult.of();
        api.setCode(base.getCode());
        api.setMsg(base.getMsg());
        api.setData(data);
        api.setStatus(Boolean.TRUE);
        return api;
    }

    public static <T> ApiResult<T> fail() {
        return fail(BaseErrorCode.SYS_ERROR);
    }

    public static <T> ApiResult<T> fail(T data) {
        return fail(BaseErrorCode.SYS_ERROR, data);
    }

    public static <T> ApiResult<T> fail(IBaseErrorCode base) {
        return fail(base, null);
    }

    public static <T> ApiResult<T> fail(IBaseErrorCode base, T data) {
        ApiResult api = ApiResult.of();
        api.setCode(base.getCode());
        api.setMsg(base.getMsg());
        api.setData(data);
        api.setStatus(Boolean.FALSE);
        return api;
    }

    public static <T> ApiResult<T> fail(BaseException base) {
        return fail(base, null);
    }

    public static <T> ApiResult<T> fail(BaseException base, T data) {
        ApiResult<T> api = ApiResult.of();
        api.setCode(base.getCode());
        api.setMsg(base.getDefaultMessage());
        api.setData(data);
        api.setStatus(Boolean.FALSE);
        return api;
    }

    /**
     * 错误传递
     *
     * @param from
     * @param <T>
     * @return
     */
    public static <T> ApiResult<T> fail(ApiResult<T> from) {
        ApiResult<T> api = ApiResult.of();
        api.setCode(from.getCode());
        api.setMsg(from.getMsg());
        api.setData(from.getData());
        api.setStatus(Boolean.FALSE);
        return api;
    }
}
