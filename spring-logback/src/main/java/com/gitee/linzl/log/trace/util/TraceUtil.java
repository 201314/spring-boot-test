package com.gitee.linzl.log.trace.util;

import com.gitee.linzl.commons.constants.GlobalConstants;
import org.slf4j.MDC;

import java.util.UUID;

/**
 * @author linzhenlie-jk
 * @date 2021/8/27
 */
public class TraceUtil {
    private static final ThreadLocal<String> TRACE_ID = new ThreadLocal<String>();

    public static String getTraceId() {
        if (TRACE_ID.get() == null) {
            String s = UUID.randomUUID().toString().replace("-", "");
            setTraceId(s);
        }
        return TRACE_ID.get();
    }

    public static void setTraceId(String traceId) {
        TRACE_ID.set(traceId);
    }
}
