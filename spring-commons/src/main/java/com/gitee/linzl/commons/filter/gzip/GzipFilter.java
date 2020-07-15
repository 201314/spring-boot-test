package com.gitee.linzl.commons.filter.gzip;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;

// @Component
// @ServletComponentScan
// @WebFilter(urlPatterns = "/login/*", filterName = "loginFilter")
public class GzipFilter extends BaseBodyResponseWrapperFilter {

    @Override
    public byte[] rewriteBody(byte[] body, HttpServletResponse resp) throws IOException {
        try (
                ByteArrayOutputStream out = new ByteArrayOutputStream(body.length);
                GZIPOutputStream gzip = new GZIPOutputStream(out)
        ) {
            gzip.write(body);
            body = out.toByteArray();
        }
        resp.setHeader("Content-Encoding", "gzip");
        return body;
    }
}
