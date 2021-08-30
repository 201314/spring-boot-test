package com.gitee.linzl.log.trace.interceptors;

import com.gitee.linzl.commons.constants.GlobalConstants;
import com.gitee.linzl.log.trace.util.TraceUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author linzhenlie-jk
 * @date 2021/8/30
 */
public class LogTraceInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        String traceID = request.getHeader(GlobalConstants.TRACE_ID);

        if (StringUtils.isBlank(traceID)) {
            traceID = TraceUtil.getTraceId();
        }
        MDC.put(GlobalConstants.TRACE_ID, traceID);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
                                @Nullable Exception ex) throws Exception {
        MDC.remove(GlobalConstants.TRACE_ID);
    }
}
