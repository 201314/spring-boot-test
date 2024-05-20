package com.gitee.linzl.bean.processor;

import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

/**
 * 比如 PropertySourcesPlaceholderConfigurer
 * 比如 SpringValueProcessor
 * 当我们调用BeanFactoryPostProcess方法时，这时候bean还没有实例化，此时bean刚被解析成BeanDefinition对象
 * Spring 中的 BeanFactoryPostProcessor 在实例化之前被调用，而 BeanPostProcessor 则是在实例化过程中使用
 *
 * @author linzhenlie-jk
 * @date 2023/7/25
 */
@Component
public class BeanFactoryPostProcessorTest implements BeanDefinitionRegistryPostProcessor, Ordered {
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        System.out.println("调用BeanFactoryPostProcessorTest的postProcessBeanFactory方法");
        if (beanFactory.containsBeanDefinition("userDomain")) {
            return;
        }

        BeanDefinition beanDefinition = beanFactory.getBeanDefinition("userDomain");
        MutablePropertyValues pv = beanDefinition.getPropertyValues();
        if (pv.contains("address")) {
            pv.addPropertyValue("address", "地址");
        }
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
    System.out.println();
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
