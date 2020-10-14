package com.gitee.linzl.condition;

import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * 判断是否是windows系统
 **/
public class WindowsCondition implements Condition {
    /**
     * @param conditionContext      判断条件能使用的上下文（环境）
     * @param annotatedTypeMetadata 注释信息
     * @return
     */
    @Override
    public boolean matches(ConditionContext conditionContext, AnnotatedTypeMetadata annotatedTypeMetadata) {
        // 能获取到IOC使用的beanFactory
        ConfigurableListableBeanFactory beanFactory = conditionContext.getBeanFactory();
        // 获取类加载器
        ClassLoader classLoader = conditionContext.getClassLoader();
        // 获取到bean定义的注册类
        BeanDefinitionRegistry registry = conditionContext.getRegistry();
        // 获取当前环境信息
        Environment environment = conditionContext.getEnvironment();
        String property = environment.getProperty("os.name");
        return property.toUpperCase().contains("WINDOWS");
    }
}
