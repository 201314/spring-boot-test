package com.gitee.linzl.commons.request;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import java.io.ByteArrayInputStream;

public class ByteArrayServletInputStream extends ServletInputStream {
    private final ByteArrayInputStream inputStream;

    public ByteArrayServletInputStream(final byte[] body) {
        this.inputStream = new ByteArrayInputStream(body);
    }

    public ByteArrayServletInputStream(final ByteArrayInputStream baos) {
        this.inputStream = baos;
    }

    @Override
    public int read() {
        return inputStream.read();
    }

    @Override
    public boolean isFinished() {
        return inputStream.available() == 0;
    }

    @Override
    public boolean isReady() {
        return true;
    }

    @Override
    public void setReadListener(final ReadListener readListener) {
        throw new RuntimeException("Not implemented");
    }
}