package com.gitee.linzl.commons.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gitee.linzl.commons.enums.BaseErrorCode;
import com.gitee.linzl.commons.enums.IBaseErrorCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 响应类
 *
 * @author linzhenlie
 * @date 2019/8/27
 */
@Setter
@Getter
public class ApiResult<T> {
    /**
     * 状态码
     */
    private String code;

    /**
     * 提示
     */
    private String msg;
    /**
     * 响应内容主体
     */
    private T data;
    /**
     * 是否成功的标识
     */
    private boolean status;
    /**
     * 请求时间
     */
    private LocalDateTime requestTime;
    /**
     * 响应时间
     */
    private LocalDateTime responseTime;

    private ApiResult() {

    }

    public ApiResult(IBaseErrorCode base) {
        this.code = base.getCode();
        this.msg = base.getMsg();
        this.status = (base == BaseErrorCode.SUCCESS);
    }

    public ApiResult(IBaseErrorCode base, T data) {
        this.code = base.getCode();
        this.msg = base.getMsg();
        this.data = data;
        this.status = (base == BaseErrorCode.SUCCESS);
    }

    /**
     * 分页时返回 {"total":1,"size":12,"pages":1,"current":1,"records":[]}
     *
     * @param data
     * @return
     */
    public ApiResult<T> data(T data) {// 表单数据,无分页的列表数据
        this.data = data;
        return this;
    }

    public static <T> ApiResult<T> success() {
        return new ApiResult<>(BaseErrorCode.SUCCESS);
    }

    public static <T> ApiResult<T> success(T data) {
        return new ApiResult(BaseErrorCode.SUCCESS, data);
    }

    public static <T> ApiResult<T> fail() {
        return new ApiResult<>(BaseErrorCode.SYS_ERROR);
    }

    public static <T> ApiResult<T> fail(T data) {
        return new ApiResult(BaseErrorCode.SYS_ERROR, data);
    }

    public static <T> ApiResult<T> fail(IBaseErrorCode base) {
        return new ApiResult<>(base);
    }

    public static <T> ApiResult<T> fail(IBaseErrorCode base, T data) {
        return new ApiResult(base, data);
    }

    /**
     * 错误传递
     *
     * @param from
     * @param <T>
     * @return
     */
    public static <T> ApiResult<T> fail(ApiResult from) {
        ApiResult<T> result = new ApiResult();
        result.setCode(from.getCode());
        result.setMsg(from.getMsg());
        result.setStatus(from.isStatus());
        return result;
    }

    @JsonIgnore
    @JSONField(serialize = false)
    public boolean isSuccess() {
        return this.status;
    }

    @JsonIgnore
    @JSONField(serialize = false)
    public boolean isFail() {
        return this.status == false;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
