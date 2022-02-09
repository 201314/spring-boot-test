package com.gitee.linzl.commons.aop;

import java.lang.reflect.Method;
import java.util.Objects;

import com.gitee.linzl.commons.annotation.RpcExceptionCatch;
import com.gitee.linzl.commons.api.ApiResult;
import com.gitee.linzl.commons.exception.BaseException;
import com.gitee.linzl.commons.tools.ApiResults;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

/**
 * 远程接口，统一异常包装
 *
 * @author linzhenlie-jk
 * @date 2020/12/22
 */
@Slf4j
@Aspect
@Component
public class RpcExceptionCatchAspect {
    /**
     * 切入点：表示在哪个类的哪个方法进行切入。配置有切入点表达式
     */
    @Pointcut("@annotation(com.gitee.linzl.commons.annotation.RpcExceptionCatch)")
    public void serviceExpression() {
    }

    /**
     * 后置异常通知 定义一个名字，该名字用于匹配通知实现方法的一个参数名，当目标方法抛出异常返回后，将把目标方法抛出的异常传给通知方法； throwing
     * 限定了只有目标方法抛出的异常与通知方法相应参数异常类型时才能执行后置异常通知，否则不执行，
     * 对于throwing对应的通知方法参数为Throwable类型将匹配任何异常。
     *
     * @param joinPoint
     * @param exception
     */
    @Around(value = "serviceExpression()")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature methodPoint = (MethodSignature) joinPoint.getSignature();
        Method method = methodPoint.getMethod();
        RpcExceptionCatch rpcExceptionCatch = method.getAnnotation(RpcExceptionCatch.class);
        Class returnType = method.getReturnType();
        // 异常情况下returnValue为空
        try {// obj之前可以写目标方法执行前的逻辑
            // 调用执行目标方法
            return joinPoint.proceed();
        } catch (Exception e) {
            log.error("环绕通知出错1", e);
            if (Objects.nonNull(rpcExceptionCatch) && returnType.isAssignableFrom(ApiResult.class)) {
                if (e instanceof BaseException) {
                    return ApiResults.fail((BaseException) e);
                }
            }
            throw e;
        } catch (Throwable e) {
            log.error("环绕通知出错2", e);
            // 一定要抛出去
            throw e;
        }
    }
}
