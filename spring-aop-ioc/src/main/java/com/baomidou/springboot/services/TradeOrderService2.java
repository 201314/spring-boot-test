package com.baomidou.springboot.services;

import com.baomidou.springboot.entity.TpTradeOrder;

/**
 * @author linzhenlie-jk
 * @date 2020/12/28
 */
public interface TradeOrderService2 {
    public void updateTrx(TpTradeOrder order, Boolean throwException);
}
