package com.baomidou.springboot.services;

import com.gitee.linzl.commons.annotation.RpcExceptionCatch;
import com.gitee.linzl.commons.api.ApiResult;
import com.gitee.linzl.commons.enums.BaseErrorCode;
import com.gitee.linzl.commons.exception.BusinessException;
import com.gitee.linzl.commons.tools.ApiResults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MyService implements IMyService {
    @Override
    public void add() {
        log.debug("========MyService add========");
    }

    @Override
    public void doSomeThing(String someThing) {
        log.debug("执行被拦截的方法：" + someThing);
    }

    @Override
    @RpcExceptionCatch
    public ApiResult doSomeThing2(String someThing) {
        log.debug("doSomeThing2：" + someThing);
        throw new BusinessException(BaseErrorCode.SERVICE_NOT_AVAILABLE);
    }

    @Override
    public void doSomeThing3(String someThing) {
        log.debug("doSomeThing3：" + someThing);
        throw new BusinessException(BaseErrorCode.SYS_ERROR);
    }

    @Override
    public ApiResult doSomeThing4(String someThing) {
        log.debug("doSomeThing4,没有异常@RpcExceptionCatch注解：" + someThing);
        throw new BusinessException(BaseErrorCode.SERVICE_NOT_AVAILABLE);
    }

    @RpcExceptionCatch
    @Override
    public ApiResult doSomeThing5(String someThing) {
        log.debug("doSomeThing4,没有异常@RpcExceptionCatch注解：" + someThing);
        int zero = 1 / 0;
        return ApiResults.success();
    }
}
