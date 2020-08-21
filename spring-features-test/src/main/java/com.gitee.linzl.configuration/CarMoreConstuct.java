package com.gitee.linzl.configuration;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Setter
@Getter
@Slf4j
@Component
public class CarMoreConstuct {
    private int maxSpeed;
    private String brand;
    private double price;

    public CarMoreConstuct(@Qualifier("user") @Autowired User user) {
        log.debug("默认1个参，user:{}", user);
    }

}