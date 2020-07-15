package com.gitee.linzl.commons.interceptor;

import java.util.Objects;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.gitee.linzl.commons.annotation.RequiredPermissionToken;
import com.gitee.linzl.commons.annotation.SkipPermissionToken;
import com.gitee.linzl.commons.api.ApiResult;
import com.gitee.linzl.commons.constants.GlobalConstants;
import com.gitee.linzl.commons.tools.ServletUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * TOKEN 默认拦截器,默认不拦截
 *
 * @author linzhenlie
 * @date 2019/8/28
 */
@Slf4j
public class DefaultPermissionTokenInterceptor extends HandlerInterceptorAdapter {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = ((HandlerMethod) handler);

        StringBuffer sb = new StringBuffer("访问目标【");
        sb.append(handlerMethod.getMethod().getDeclaringClass()).append(".").append(handlerMethod.getMethod().getName())
                .append("】");
        SkipPermissionToken skipPermissionToken = handlerMethod.getMethod().getAnnotation(SkipPermissionToken.class);
        if (Objects.nonNull(skipPermissionToken)) {
            log.debug("【{}】,方法不用验证Token", sb.toString());
            return true;
        }

        RequiredPermissionToken requiredPermissionToken = handlerMethod.getMethod().getDeclaringClass().getAnnotation(RequiredPermissionToken.class);
        if (Objects.isNull(requiredPermissionToken)) {
            log.debug("【{}】,类不用验证Token", handlerMethod.getMethod().getDeclaringClass());
            return true;
        }
        ApiResult resp = processToken(request, response, handler);
        if (resp.isSuccess()) {
            return true;
        }
        ServletUtil.renderJSON(response, resp.toString());
        return false;
    }

    /**
     * @param request
     * @param response
     * @param handler
     * @return boolean
     */
    public ApiResult processToken(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String token = request.getHeader(GlobalConstants.ACCESS_TOKEN);
        return checkToken(token);
    }

    /**
     * 提供给子类去实现token的校验
     *
     * @param token
     * @return
     */
    protected ApiResult checkToken(String token) {
        return ApiResult.success();
    }
}
