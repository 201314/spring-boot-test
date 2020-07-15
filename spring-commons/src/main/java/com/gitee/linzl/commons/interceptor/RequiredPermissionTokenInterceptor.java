package com.gitee.linzl.commons.interceptor;

import com.gitee.linzl.commons.api.ApiResult;
import com.gitee.linzl.commons.autoconfigure.token.ITokenService;
import lombok.extern.slf4j.Slf4j;

/**
 * TOKEN 资源拦截器
 *
 * @author linzhenlie
 * @date 2019/8/28
 */
@Slf4j
public class RequiredPermissionTokenInterceptor extends DefaultPermissionTokenInterceptor {
    private ITokenService iTokenService;

    public RequiredPermissionTokenInterceptor(ITokenService iTokenService) {
        this.iTokenService = iTokenService;
    }

    /**
     * 提供给子类去实现token的校验
     *
     * @param token
     * @return
     */
    @Override
    protected ApiResult checkToken(String token) {
        return ApiResult.success();
    }
}
