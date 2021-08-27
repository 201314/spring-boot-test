package com.gitee.linzl.log.trace.feign;

import com.gitee.linzl.commons.constants.GlobalConstants;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.slf4j.MDC;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Component;

/**
 * @author linzhenlie-jk
 * @date 2021/8/27
 */
@ConditionalOnClass(RequestInterceptor.class)
@Component
public class LogTraceRequestInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate template) {
        addHeader(template, GlobalConstants.TRACE_ID, MDC.get(GlobalConstants.TRACE_ID));
    }

    protected void addHeader(RequestTemplate requestTemplate, String name, String... values) {
        if (!requestTemplate.headers().containsKey(name)) {
            requestTemplate.header(name, values);
        }
    }
}
