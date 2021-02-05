package com.baomidou.springboot.entity;

import com.gitee.linzl.commons.api.BaseEntity;
import com.gitee.linzl.commons.mybatis.annotation.Encrypted;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
public class Product extends BaseEntity {
    private Long id;

    @Encrypted
    private String name;
    private String nameMd5x;
    private String nameEncrypt;

    private String category;
    private String detail;
    private BigDecimal buyPrice;
}