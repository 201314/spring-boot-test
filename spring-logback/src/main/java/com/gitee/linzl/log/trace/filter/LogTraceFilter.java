package com.gitee.linzl.log.trace.filter;

import com.gitee.linzl.commons.constants.GlobalConstants;
import com.gitee.linzl.log.trace.util.TraceUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * logback 内置实现 MDCInsertingServletFilter
 *
 * @author linzhenlie-jk
 * @date 2021/8/27
 */
@Component
@ServletComponentScan
@WebFilter(urlPatterns = "/*")
public class LogTraceFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String traceID = request.getHeader(GlobalConstants.TRACE_ID);

        if (StringUtils.isBlank(traceID)) {
            traceID = TraceUtil.getTraceId();
        }
        MDC.put(GlobalConstants.TRACE_ID, traceID);
        filterChain.doFilter(request, response);
        //MDC.remove(GlobalConstants.TRACE_ID);
    }
}
