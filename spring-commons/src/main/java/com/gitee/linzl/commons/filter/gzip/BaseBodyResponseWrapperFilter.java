package com.gitee.linzl.commons.filter.gzip;

import com.gitee.linzl.commons.request.BaseBodyResponseWrapper;
import com.gitee.linzl.commons.tools.ServletUtil;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

/**
 * 继承后，重写rewriteBody方法,对body的内容进行修改后返回字节，将自动对其放回reponse的流中
 * <p>
 * 如果是需要两个OutputStream的方式,可以使用apache的TeeOutputStream
 *
 * @author linzhenlie
 * @date 2019-12-26
 */
@Slf4j
public class BaseBodyResponseWrapperFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        if (!ServletUtil.isGzipSupported(req)) {
            chain.doFilter(request, response);
            return;
        }

        // 把增强的resp放进去,放行到后台(在后台把数据写到ByteArrayOutputStream中)
        BaseBodyResponseWrapper wrappedResponse = new BaseBodyResponseWrapper(resp);
        byte[] srcBytes = wrappedResponse.toByteArray();
        log.debug("压缩前srcBytes的长度:【{}】", srcBytes.length);

        if (Objects.isNull(srcBytes) || srcBytes.length == 0) {
            chain.doFilter(request, response);
            return;
        }

        chain.doFilter(request, wrappedResponse);

        byte[] destBytes = rewriteBody(srcBytes, resp);
        resp.setContentLength(destBytes.length);
        resp.getOutputStream().write(destBytes);
    }

    public byte[] rewriteBody(byte[] originalBody, HttpServletResponse resp) throws IOException {
        return originalBody;
    }
}
