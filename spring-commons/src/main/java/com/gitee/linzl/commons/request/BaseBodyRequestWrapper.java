package com.gitee.linzl.commons.request;

import org.springframework.util.StreamUtils;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * 继承后，重写rewriteBody方法,对body的内容进行修改后返回字节，将自动对其放回request的流中
 *
 * @author linzhenlie
 * @date 2019-12-26
 */
public class BaseBodyRequestWrapper extends HttpServletRequestWrapper {
    private final byte[] body;

    public BaseBodyRequestWrapper(HttpServletRequest request) throws IOException {
        super(request);
        //由于request并没有提供现成的获取json字符串的方法，所以我们需要将body中的流转为字符串
        body = StreamUtils.copyToByteArray(request.getInputStream());
        rewriteBody(body);
    }

    public byte[] rewriteBody(byte[] body) {
        return body;
    }

    /**
     * 在使用@RequestBody注解的时候，其实框架是调用了getInputStream()方法，所以我们要重写这个方法
     *
     * @return
     * @throws IOException
     */
    @Override
    public ServletInputStream getInputStream() throws IOException {
        return new ByteArrayServletInputStream(body);
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(getInputStream()));
    }
}