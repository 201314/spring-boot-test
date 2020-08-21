package com.gitee.linzl.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MyTestConfig {

    @Bean
    public ConfigurationTest driver() {
        ConfigurationTest driver = new ConfigurationTest();
        driver.setName("driver");
        driver.setName("driver地址");
        driver.setTest2(car());
        return driver;
    }

    @Bean
    public ConfigurationTest2 car() {
        ConfigurationTest2 car = new ConfigurationTest2();
        car.setName("car");
        car.setAddress("car地址");
        return car;
    }
}