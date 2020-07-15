package com.gitee.linzl.commons.aop;


import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Method;

/**
 * 自动填充公共字段
 *
 * @author linzhenlie
 * @date 2019/9/5
 */
@ConditionalOnClass(name = "org.apache.ibatis.annotations.Mapper")
@Configuration
@Aspect
@Slf4j
public class AutoFillBaseEntityAspect {
    @Pointcut("execution(* com.baomidou.mybatisplus.core.mapper.BaseMapper.*(..))")
    public void daoParentExpression() {

    }

    @Pointcut("within(@org.apache.ibatis.annotations.Mapper *)")
    public void daoExpression() {

    }

    @Around("daoExpression()||daoParentExpression()")
    public Object doAround(ProceedingJoinPoint point) throws Throwable {
        MethodSignature methodPoint = (MethodSignature) point.getSignature();
        Method method = methodPoint.getMethod();

        String methodName = method.getName();
        Object[] args = point.getArgs();
        // 使用mybatis拦截器完成
        /*// 假删除
        if (methodName.startsWith("del") || methodName.startsWith("remove")) {
            for (int j = 0; j < args.length; j++) {
                if (args[j] instanceof BaseDelEntity) {
                    BaseDelEntity entity = (BaseDelEntity) args[j];
                    entity.setIsDeleted(1);
                    entity.setDeletedBy("你");
                    entity.setDeletedTime(LocalDateTime.now());
                }
            }
        }
        //修改
        else if (methodName.startsWith("update") || methodName.startsWith("modify")) {
            for (int j = 0; j < args.length; j++) {
                if (args[j] instanceof BaseUpdateEntity) {
                    BaseUpdateEntity entity = (BaseUpdateEntity) args[j];
                    entity.setUpdatedBy("他11");
                    entity.setUpdatedTime(LocalDateTime.now());
                }
            }
        }
        //增加
        else if (methodName.startsWith("add") || methodName.startsWith("insert")) {
            for (int j = 0; j < args.length; j++) {
                if (args[j] instanceof BaseAddEntity) {
                    BaseAddEntity entity = (BaseAddEntity) args[j];
                    entity.setCreatedBy("我");
                    entity.setCreatedTime(LocalDateTime.now());
                }
            }
        }*/


        Object retVal = null;
        try {
            retVal = point.proceed();
        } catch (Throwable e) {
            throw e;
        }
        return retVal;
    }

}
