package com.baomidou.springboot.aop;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import lombok.extern.slf4j.Slf4j;

/**
 * 使用拦截器的方式要在启动时进行编码注册
 *
 * @author linzhenlie
 * @date 2019/8/28
 */
@Slf4j
public class MyMethodInterceptor implements MethodInterceptor {
    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        Object result = null;
        try {
            log.debug("=======方法执行之前：" + methodInvocation.getMethod().toString());

            result = methodInvocation.proceed();

            log.debug("=======方法执行之后：" + methodInvocation.getMethod().toString());
            log.debug("=======方法正常运行结果：" + result);
            return result;
        } catch (Exception e) {
            log.debug("=======方法出现异常:" + e.toString());
            log.debug("=======方法运行Exception结果：" + result);
            return result;
        }
    }
}