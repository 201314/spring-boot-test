package com.gitee.linzl.configuration;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Setter
@Getter
@Slf4j
public class Car {
    private int maxSpeed;
    private String brand;
    private double price;
}