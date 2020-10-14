package com.gitee.linzl.imports;

import com.gitee.linzl.domain.CarDomain;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

public class CustomImportBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar {
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
            // 指定Bean定义信息（Bean类型：Bean）
            RootBeanDefinition rootBeanDefinition = new RootBeanDefinition(CarDomain.class);
            // 注册一个Bean，指定Bean名
            registry.registerBeanDefinition("CarDomainRigstrar", rootBeanDefinition);
        }
    }
}
