package com.gitee.linzl.commons.tools;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;

import java.util.Map;
import java.util.Objects;

/**
 * copy from apollo client
 * 注册一个bean信息
 *
 * @author linzhenlie
 * @date 2020-07-02
 * @see com.ctrip.framework.apollo.spring.annotation.ApolloConfigRegistrar
 * @see org.springframework.context.annotation.ImportBeanDefinitionRegistrar
 * @see com.ctrip.framework.apollo.spring.config.ConfigPropertySourcesProcessor
 * @see org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor
 */
public class BeanRegistrationUtil {
    public static boolean registerBeanDefinitionIfNotExists(BeanDefinitionRegistry registry, String beanName,
                                                            Class<?> beanClass) {
        return registerBeanDefinitionIfNotExists(registry, beanName, beanClass, null);
    }

    /**
     * 当bean不存在时注册bean
     *
     * @param registry            spring容器
     * @param beanName            注册bean的名称
     * @param beanClass           需要注册的beanClass
     * @param extraPropertyValues 添加bean的属性及属性值
     * @return
     */
    public static boolean registerBeanDefinitionIfNotExists(BeanDefinitionRegistry registry, String beanName,
                                                            Class<?> beanClass, Map<String, Object> extraPropertyValues) {
        // 容器中是否已经存在该bean
        if (registry.containsBeanDefinition(beanName)) {
            return false;
        }

        // 所有候选bean
        String[] candidates = registry.getBeanDefinitionNames();
        for (String candidate : candidates) {
            BeanDefinition beanDefinition = registry.getBeanDefinition(candidate);
            if (Objects.equals(beanDefinition.getBeanClassName(), beanClass.getName())) {
                return false;
            }
        }

        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(beanClass);
        if (Objects.nonNull(extraPropertyValues)) {
            // 设置普通类型属性值
            extraPropertyValues.entrySet().stream().forEach(entry -> builder.addPropertyValue(entry.getKey(), entry.getValue()));
        }
        //获取BeanDefinition
        BeanDefinition beanDefinition = builder.getBeanDefinition();
        //向bean注册器(容器)中注册bean
        registry.registerBeanDefinition(beanName, beanDefinition);
        return true;
    }
}
