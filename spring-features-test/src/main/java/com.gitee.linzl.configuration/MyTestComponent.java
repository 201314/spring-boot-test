package com.gitee.linzl.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class MyTestComponent {

    @Bean
    public ComponentTest driver1() {
        ComponentTest driver = new ComponentTest();
        driver.setName("driver");
        driver.setName("driver地址");
        driver.setTest2(car1());
        return driver;
    }

    @Bean
    public ComponentTest2 car1() {
        ComponentTest2 car = new ComponentTest2();
        car.setName("car");
        car.setAddress("car地址");
        return car;
    }
}