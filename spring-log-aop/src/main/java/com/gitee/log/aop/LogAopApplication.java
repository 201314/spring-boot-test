package com.gitee.log.aop;

import com.gitee.linzl.EnableAutoCommons;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

@EnableAutoCommons
public class LogAopApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(LogAopApplication.class, args);
        for (String beanDefinitionName : context.getBeanDefinitionNames()) {
            System.out.println(beanDefinitionName + "==>" + context.getBean(beanDefinitionName));
        }
    }

}
