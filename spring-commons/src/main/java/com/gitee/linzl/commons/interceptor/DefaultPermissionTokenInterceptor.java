package com.gitee.linzl.commons.interceptor;

import com.gitee.linzl.commons.annotation.RequiredPermissionToken;
import com.gitee.linzl.commons.annotation.SkipPermissionToken;
import com.gitee.linzl.commons.api.ApiResult;
import com.gitee.linzl.commons.autoconfigure.token.ITokenService;
import com.gitee.linzl.commons.constants.GlobalConstants;
import com.gitee.linzl.commons.tools.ServletUtil;
import com.gitee.linzl.thread.UserContext;
import com.gitee.linzl.thread.UserContextHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

/**
 * TOKEN 默认拦截器
 *
 * @author linzhenlie
 * @date 2019/8/28
 */
@Slf4j
public class DefaultPermissionTokenInterceptor extends HandlerInterceptorAdapter {
    @Autowired
    private ITokenService iTokenService;

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
    private ApiResult processToken(HttpServletRequest request, HttpServletResponse response, Object handler) {
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
        String userNo = iTokenService.parse(token);
        UserContext context = new UserContext();
        context.setUserNo(userNo);
        UserContextHandler.setContext(context);
        return ApiResult.success();
    }
}
