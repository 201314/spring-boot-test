package com.gitee.linzl.domain;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CarDomain {
    private int maxSpeed;
    private String brand;
    private double price;

    public CarDomain() {

    }

    public CarDomain(String brand) {
        this.brand = brand;
    }

    public void init() {
        System.out.println("car init ………… ");
    }

    public void destroy() {
        System.out.println(" ………… car destroy");
    }
}