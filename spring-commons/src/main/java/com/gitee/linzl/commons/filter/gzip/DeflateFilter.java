package com.gitee.linzl.commons.filter.gzip;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPOutputStream;

// @Component
// @ServletComponentScan
// @WebFilter(urlPatterns = "/login/*", filterName = "loginFilter")
public class DeflateFilter extends BaseBodyResponseWrapperFilter {

    @Override
    public byte[] rewriteBody(byte[] body, HttpServletResponse resp) throws IOException {
        Deflater defeater = new Deflater(Deflater.BEST_SPEED);
        try (
                ByteArrayOutputStream out = new ByteArrayOutputStream(body.length);
                DeflaterOutputStream deflater = new DeflaterOutputStream(out, defeater)
        ) {
            deflater.write(body);
            body = out.toByteArray();
        } catch (IOException e) {
            throw e;
        } finally {
            defeater.end();
        }
        resp.setHeader("Content-Encoding", "deflate");
        return body;
    }
}
