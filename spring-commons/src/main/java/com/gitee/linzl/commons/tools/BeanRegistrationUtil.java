package com.gitee.linzl.commons.tools;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;

import java.util.Map;
import java.util.Objects;

/**
 * copy from apollo client
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

    public static boolean registerBeanDefinitionIfNotExists(BeanDefinitionRegistry registry, String beanName,
                                                            Class<?> beanClass, Map<String, Object> extraPropertyValues) {
        if (registry.containsBeanDefinition(beanName)) {
            return false;
        }

        String[] candidates = registry.getBeanDefinitionNames();

        for (String candidate : candidates) {
            BeanDefinition beanDefinition = registry.getBeanDefinition(candidate);
            if (Objects.equals(beanDefinition.getBeanClassName(), beanClass.getName())) {
                return false;
            }
        }

        BeanDefinition beanDefinition = BeanDefinitionBuilder.genericBeanDefinition(beanClass).getBeanDefinition();
        if (Objects.nonNull(extraPropertyValues)) {
            for (Map.Entry<String, Object> entry : extraPropertyValues.entrySet()) {
                beanDefinition.getPropertyValues().add(entry.getKey(), entry.getValue());
            }
        }

        registry.registerBeanDefinition(beanName, beanDefinition);
        return true;
    }
}
