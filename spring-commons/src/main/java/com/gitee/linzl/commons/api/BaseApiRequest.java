package com.gitee.linzl.commons.api;

import java.beans.Transient;

/**
 * @author linzhenlie-jk
 * @date 2021/6/25
 */
public interface BaseApiRequest<T extends BaseApiResponse> {

    /**
     * 请求的链接
     *
     * @return
     */
    @Transient
    default String getUrl() {
        return "";
    }

    /**
     * 请求的API方法
     *
     * @return
     */
    @Transient
    public abstract String getMethod();


    /**
     * 判断是否需要加密
     *
     * @return
     */
    @Transient
    default boolean isNeedEncrypt() {
        return false;
    }

    /**
     * 设置请求是否需要加密
     *
     * @param needEncrypt
     */
    public void setNeedEncrypt(boolean needEncrypt);
}
