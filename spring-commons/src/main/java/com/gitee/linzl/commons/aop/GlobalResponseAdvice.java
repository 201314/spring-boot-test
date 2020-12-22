package com.gitee.linzl.commons.aop;

import com.gitee.linzl.commons.api.ApiResult;
import com.gitee.linzl.commons.tools.ApiResults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * 全局统一返回的处理器，此种方式较少可忽略
 *
 * @author linzl
 * @date 2020/7/20
 */
//@RestControllerAdvice(basePackages = "com.gitee.linzl")
@Slf4j
public class GlobalResponseAdvice implements ResponseBodyAdvice {
    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class selectedConverterType,
                                  ServerHttpRequest request, ServerHttpResponse response) {
        if (body instanceof ApiResult) {
            return body;
        }
        // 如果不是，包装成统一类型，且约定为成功
        return ApiResults.success(body);
    }

}