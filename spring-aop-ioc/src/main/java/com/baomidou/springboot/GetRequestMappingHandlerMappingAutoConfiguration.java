package com.baomidou.springboot;

import com.gitee.linzl.commons.annotation.ApiMethod;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.config.annotation.DelegatingWebMvcConfiguration;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.Objects;

@Configuration
@Slf4j
@AutoConfigureAfter(DelegatingWebMvcConfiguration.class)
public class GetRequestMappingHandlerMappingAutoConfiguration {

    @Autowired
    private RequestMappingHandlerMapping mapping;

    @PostConstruct
    public void hello() {
        Map<RequestMappingInfo, HandlerMethod> map = mapping.getHandlerMethods();
        map.forEach((requestMappingInfo, handlerMethod) -> {
            ApiMethod api = handlerMethod.getMethodAnnotation(ApiMethod.class);
            log.warn("url:【{}】,method:【{}】,apiMethod:【{}】",

                    requestMappingInfo.getPatternsCondition().getPatterns(),

                    handlerMethod,

                    (Objects.nonNull(api) ? api.value() : "没有"));
        });
    }
}
