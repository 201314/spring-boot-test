package com.gitee.linzl.configuration;

import com.gitee.linzl.domain.ComponentDomain;
import com.gitee.linzl.domain.ComponentDomain2;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class ComponentTeset {

    @Bean
    public ComponentDomain driver1() {
        ComponentDomain driver = new ComponentDomain();
        driver.setName("driver");
        driver.setName("driver地址");
        driver.setTest2(car1());
        return driver;
    }

    @Bean
    public ComponentDomain2 car1() {
        ComponentDomain2 car = new ComponentDomain2();
        car.setName("car");
        car.setAddress("car地址");
        return car;
    }
}