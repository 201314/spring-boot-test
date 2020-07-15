package com.gitee.log.aop.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gitee.linzl.commons.api.BaseEntity;
import com.gitee.log.aop.datalog.OperationLog;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

@Setter
@Getter
@TableName(value="t_product")
public class Product extends BaseEntity {
    @TableId
    private Long id;

    @OperationLog(name = "产品名称")
    @NotNull
    private String name;

    private String category;

    private String detail;

    private BigDecimal buyPrice;

    private BigDecimal sellPrice;

    private String provider;

    private Date onlineTime;

    private Date updateTime;
}
