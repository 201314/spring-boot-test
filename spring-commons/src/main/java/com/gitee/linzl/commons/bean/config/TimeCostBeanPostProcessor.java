package com.gitee.linzl.commons.bean.config;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.stereotype.Component;

/**
 * bean初始化耗时跟踪
 */
@Slf4j
@Component
public class TimeCostBeanPostProcessor implements InstantiationAwareBeanPostProcessor {
    private Map<String, Long> costMap = new ConcurrentHashMap<>(125);

    /**
     * bean初始化之前会调用的方法
     */
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        costMap.put(beanName, System.currentTimeMillis());
        return bean;
    }

    /**
     * bean初始化之后会调用的方法
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        long cost = System.currentTimeMillis() - costMap.get(beanName);
        log.info("class:{},bean:{},time milliseconds:{}", bean.getClass().getName(), beanName, cost);
        return bean;
    }
}