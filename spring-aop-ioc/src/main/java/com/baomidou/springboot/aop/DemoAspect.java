package com.baomidou.springboot.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@Aspect
@Component
@Slf4j
public class DemoAspect {
    /**
     * 切入点：表示在哪个类的哪个方法进行切入。配置有切入点表达式
     */
    @Pointcut("execution(* com.baomidou.springboot.controller.*Rest.*(..))")
    public void serviceExpression() {
        log.debug("@Pointcut==>serviceExpression");
    }

    /**
     * 前置通知，方法调用前被调用
     *
     * @param joinPoint
     */
    @Before("serviceExpression()")
    public void before(JoinPoint joinPoint) {
        log.debug("我是前置通知!!!");
        // 获取目标方法的参数信息
        Object[] obj = joinPoint.getArgs();
        // AOP代理类的信息
        joinPoint.getThis();
        // 代理的目标对象
        joinPoint.getTarget();
        // 用的最多 通知的签名
        Signature signature = joinPoint.getSignature();
        // 代理的是哪一个方法
        System.out.println(signature.getName());
        // AOP代理类的名字
        System.out.println(signature.getDeclaringTypeName());
        // AOP代理类的类（class）信息
        signature.getDeclaringType();
        // 获取RequestAttributes
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();

        // 从获取RequestAttributes中获取HttpServletRequest的信息
        // HttpServletRequest request = (HttpServletRequest) requestAttributes
        // .resolveReference(RequestAttributes.REFERENCE_REQUEST);
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) requestAttributes;
        HttpServletRequest request = servletRequestAttributes.getRequest();

        // 如果要获取Session信息的话，可以这样写：
        // HttpSession session = (HttpSession)
        // requestAttributes.resolveReference(RequestAttributes.REFERENCE_SESSION);
        HttpSession session = request.getSession();

        Enumeration<String> enumeration = request.getParameterNames();
        Map<String, String> parameterMap = new HashMap<>();
        while (enumeration.hasMoreElements()) {
            String parameter = enumeration.nextElement();
            parameterMap.put(parameter, request.getParameter(parameter));
        }
        if (obj.length > 0) {
            log.debug("请求的参数信息为：" + parameterMap);
        }
    }

    /**
     * 环绕通知： 环绕通知非常强大，可以决定目标方法是否执行，什么时候执行，执行时是否需要替换方法参数，执行完毕是否需要替换返回值。
     * 环绕通知第一个参数必须是org.aspectj.lang.ProceedingJoinPoint类型
     */
    @Around("serviceExpression()")
    public Object aroundMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        log.debug("环绕通知的目标方法名：" + joinPoint.getSignature().getName());
        try {// obj之前可以写目标方法执行前的逻辑
            // 调用执行目标方法
            return joinPoint.proceed();
        } catch (Throwable e) {
            //log.error("环绕通知出错", e);
            // 一定要抛出去
            throw e;
        }
    }

    /**
     * 后置最终通知（目标方法只要执行完了就会执行后置通知方法）
     *
     * @param joinPoint
     */
    @After("serviceExpression()")
    public Object doAfter(JoinPoint joinPoint) {
        log.debug("后置通知");
        return null;
    }

    /**
     * 后置返回通知 这里需要注意的是: 如果参数中的第一个参数为JoinPoint，则第二个参数为返回值的信息
     * 如参数中的第一个参数不为JoinPoint，则第一个参数为returning中对应的参数
     * <p>
     * returning限定了只有目标方法返回值与通知方法相应参数类型时才能执行后置返回通知，否则不执行，
     * <p>
     * 对于returning对应的通知方法参数为Object类型将匹配任何目标返回值
     *
     * @param joinPoint
     * @param keys
     */
    @AfterReturning(value = "serviceExpression()", returning = "keys")
    public Object doAfterReturning(JoinPoint joinPoint, Object keys) {
        log.debug("后置返回通知的返回值：" + keys);
        return keys;
    }

    /**
     * 后置异常通知 定义一个名字，该名字用于匹配通知实现方法的一个参数名，当目标方法抛出异常返回后，将把目标方法抛出的异常传给通知方法； throwing
     * 限定了只有目标方法抛出的异常与通知方法相应参数异常类型时才能执行后置异常通知，否则不执行，
     * 对于throwing对应的通知方法参数为Throwable类型将匹配任何异常。
     * 该方法不会将异常捕捉，spring会继续往外抛出
     *
     * @param joinPoint
     * @param exception
     */
    @AfterThrowing(value = "serviceExpression()", throwing = "exception")
    public Object doAfterThrowing(JoinPoint joinPoint, Throwable exception) {
        // 异常情况下returnValue为空
        // 目标方法名：
        log.error("发生了异常:【{}】", joinPoint.getSignature().getName());
        if (exception instanceof NullPointerException) {
            log.debug("发生了空指针异常!!!!!");
        }
        return null;
    }
}