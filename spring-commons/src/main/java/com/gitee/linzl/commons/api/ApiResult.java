package com.gitee.linzl.commons.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 响应类 ,对应工具类为 com.gitee.linzl.commons.tools.ApiResults
 *
 * @author linzhenlie
 * @date 2019/8/27
 */
@Setter
@Getter
public class ApiResult<T> implements Serializable {
    private static final long serialVersionUID = -422085899035954488L;
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

    public ApiResult() {

    }

    public static ApiResult of() {
        return new ApiResult();
    }

    @JsonIgnore
    @JSONField(serialize = false)
    public boolean isSuccess() {
        return this.status == true;
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
