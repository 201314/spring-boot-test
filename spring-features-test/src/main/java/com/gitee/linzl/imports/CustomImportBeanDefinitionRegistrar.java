package com.gitee.linzl.imports;

import com.gitee.linzl.domain.CarDomain;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

public class CustomImportBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar {
    /**
     * @param importingClassMetadata  AnnotationMetadata类型的，通过这个可以获取被@Import注解标注的类所有注解的信息。
     * @param registry                BeanDefinitionRegistry类型，是一个接口，内部提供了注册bean的各种方法。
     * @param importBeanNameGenerator BeanNameGenerator类型，是一个接口，内部有一个方法，用来生成bean的名称。
     */
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry,
                                        BeanNameGenerator importBeanNameGenerator) {
        registerBeanDefinitions(importingClassMetadata, registry);
    }

    /**
     * 把所有需要添加到容器中的bean，调用BeanDefinitionRegistry的registerBeanDefinition手工注册进来
     *
     * @param importingClassMetadata 当前类的注解信息
     * @param registry               BeanDefinition注册类
     */
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        boolean redFlg = registry.containsBeanDefinition("com.shunxi.bean.Red");
        boolean yellowFlg = registry.containsBeanDefinition("com.shunxi.bean.Yellow");
        if (redFlg && yellowFlg) {
            // 指定Bean定义信息
            BeanDefinition rootBeanDefinition = BeanDefinitionBuilder.genericBeanDefinition(CarDomain.class).getBeanDefinition();
            // 注册一个Bean，指定Bean名
            registry.registerBeanDefinition("CarDomainRigstrar", rootBeanDefinition);
        }
    }
}
