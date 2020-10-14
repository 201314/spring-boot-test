package com.gitee.linzl.configuration;

import com.gitee.linzl.domain.ConfigurationDomain;
import com.gitee.linzl.domain.ConfigurationDomain2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfigurationTest {

    @Bean
    public ConfigurationDomain driver() {
        ConfigurationDomain driver = new ConfigurationDomain();
        driver.setName("driver");
        driver.setName("driver地址");
        // 级联依赖
        driver.setTest2(car());
        return driver;
    }

    @Bean("car2")
    public ConfigurationDomain2 car() {
        ConfigurationDomain2 car = new ConfigurationDomain2();
        car.setName("car");
        car.setAddress("car地址");
        return car;
    }
}