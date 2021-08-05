package com.gitee.linzl.commons.client;

/**
 * @author linzhenlie-jk
 * @date 2021/6/30
 */
public interface IHttpClient {
    public String post(String url, String data, String apiMethod);
}
