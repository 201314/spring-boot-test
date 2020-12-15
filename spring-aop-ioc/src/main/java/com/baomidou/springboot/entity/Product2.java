package com.baomidou.springboot.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gitee.linzl.commons.api.BaseEntity;
import com.gitee.linzl.commons.mybatis.annotation.Encrypted;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
@TableName("test_product")
public class Product2 extends BaseEntity {
    @TableId
    private Long id;

    @Encrypted
    private String name;
    private String nameMd5x;
    private String nameEncrypt;

    private String category;
    private String detail;
    private BigDecimal buyPrice;
}