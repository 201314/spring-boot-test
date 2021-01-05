package com.gitee.linzl.bean.processor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class InstantiationTracingBeanPostProcessor implements BeanPostProcessor {
    /**
     * bean初始化之前会调用的方法
     */
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        return bean;
    }

    /**
     * bean初始化之后会调用的方法
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        //log.debug("======================Bean '" + beanName + "' created : " + bean.toString());
        return bean;
    }
}