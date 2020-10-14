package com.gitee.linzl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
@Slf4j
public class SpringFeatureTest {

    public static void main(String[] args) {
        ConfigurableApplicationContext context
                = SpringApplication.run(SpringFeatureTest.class, args);
        log.debug("=======================打印bean=======================");
        for (String beanDefinitionName : context.getBeanDefinitionNames()) {
            System.err.println(beanDefinitionName + "-->" + context.getBean(beanDefinitionName));
        }
    }

}
