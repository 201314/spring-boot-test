package com.baomidou.springboot.services;

import com.baomidou.springboot.entity.TpTradeOrder;
import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * @author linzhenlie-jk
 * @date 2020/12/28
 */
public interface TradeOrderService {
    public void saveTrx(Long id);

    public void updateTrx(TpTradeOrder order, Boolean throwException);

    public void doBiz1NoException() throws JsonProcessingException;

    public void doBiz1NoExceptionTrx() throws JsonProcessingException;

    /**
     * doBiz5TrxAndThis 是用来验证this调用，事务失效的现象
     */
    public void doBiz5TrxAndThis();

    /**
     * doBiz5TrxAndThisTrx 是用来验证this调用，事务失效的现象
     */
    public void doBiz5TrxAndThisTrx();

    public void testTrx();
}
