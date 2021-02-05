package com.baomidou.springboot.services;

import com.baomidou.springboot.entity.TradeOrder;

/**
 * @author linzhenlie-jk
 * @date 2020/12/28
 */
public interface TradeOrderService {
    public void save(TradeOrder order);

    public void updateTrx(TradeOrder order);

    public TradeOrder select(Long id);

    public void doBiz11();
    public void doBiz12();

    public void doBiz1();
    public void doBiz1Trx();

    public void doBiz2();
    public void doBiz2Trx();

    public void doBiz3();
    public void doBiz3Trx();

    public void doBiz4();
    public void doBiz4Trx();
}
