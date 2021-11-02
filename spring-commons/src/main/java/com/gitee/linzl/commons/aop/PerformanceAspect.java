package com.gitee.linzl.commons.aop;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gitee.linzl.commons.tools.UserClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.UUID;

/**
 * 耗时切面
 *
 * @author linzl
 * @description
 * @email 2225010489@qq.com
 * @date 2019年4月8日
 */
@Configuration
@Aspect
@Slf4j
public class PerformanceAspect {
    @Autowired
    private ObjectMapper mapper;


    @Around("@annotation(com.gitee.linzl.commons.annotation.Performance)")
    public Object doAround(ProceedingJoinPoint point) throws Throwable {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) requestAttributes;

        HttpServletRequest request = servletRequestAttributes.getRequest();

        StopWatch clock = new StopWatch(UUID.randomUUID().toString());
        LocalDateTime startTime = LocalDateTime.now();
        clock.start();

        Object result = null;
        try {
            result = point.proceed();
            return result;
        } catch (Exception t) {
            throw t;
        } finally {
            clock.stop();
            MethodSignature methodPoint = (MethodSignature) point.getSignature();
            Method method = methodPoint.getMethod();

            Object json = null;

            Annotation[][] parameterAnnotations = method.getParameterAnnotations();
            for (int i = 0; i < parameterAnnotations.length; i++) {
                boolean flag = false;
                for (int j = 0; j < parameterAnnotations[i].length; j++) {
                    if (parameterAnnotations[i][j] instanceof RequestBody) {
                        json = point.getArgs()[i];
                        flag = true;
                        break;
                    }
                }
                if (flag) {
                    break;
                }
            }

            if (log.isDebugEnabled()) {
                StringBuilder sbLog = new StringBuilder();
                sbLog.append("IP【").append(UserClientUtil.builder(request).getIp()).append("】,")

                        .append("key【").append(clock.getId()).append("】,")
                        .append("uri【").append(request.getRequestURI()).append("】,")
                        .append("method【").append(request.getMethod()).append("】,")

                        .append("访问目标【").append(method.getDeclaringClass().getName()).append("#").append(method.getName()).append("】,")
                        .append("执行开始时间【").append(startTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss" +
                        ".SSS"))).append("】,")
                        .append("执行耗时【").append(clock.getTotalTimeMillis()).append(" ms").append("】,")

                        .append("输入数据【").append(mapper.writeValueAsString(json)).append("】,")
                        .append("输出数据【").append(mapper.writeValueAsString(result)).append("】");
                log.debug(sbLog.toString());
            }
        }
    }
}
