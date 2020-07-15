package com.gitee.linzl.commons.request;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Objects;

/**
 * 继承后，重写rewriteBody方法,对body的内容进行修改后返回字节，将自动对其放回request的流中
 * <p>
 * 如果是需要两个OutputStream的方式,可以使用apache的TeeOutputStream
 *
 * @author linzhenlie
 * @date 2019-12-26
 */
public class BaseBodyResponseWrapper extends HttpServletResponseWrapper {
    private ByteArrayOutputStream out;
    private ServletOutputStream outputStream;

    public BaseBodyResponseWrapper(HttpServletResponse response) {
        super(response);
        out = new ByteArrayOutputStream();
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        if (Objects.nonNull(outputStream)) {
            return outputStream;
        }
        return outputStream = new ServletOutputStream() {
            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setWriteListener(WriteListener writeListener) {

            }

            @Override
            public void write(int b) throws IOException {
                out.write(b);
            }
        };
    }

    public byte[] toByteArray() {
        if (Objects.nonNull(out)) {
            try {
                this.out.flush();
                return this.out.toByteArray();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }
}