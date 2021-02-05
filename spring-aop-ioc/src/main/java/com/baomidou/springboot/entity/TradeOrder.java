package com.baomidou.springboot.entity;

import com.gitee.linzl.commons.api.BaseEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * @author linzhenlie-jk
 * @date 2020/12/28
 */
@Setter
@Getter
public class TradeOrder extends BaseEntity {
    private Long id;

    /**
     * 订单状态： 1 待支付，2支付中，3支付成功，4支付失败，5支付关闭
     */
    private String tradeStatus;
    /**
     * 随意
     */
    private String hello;
}
