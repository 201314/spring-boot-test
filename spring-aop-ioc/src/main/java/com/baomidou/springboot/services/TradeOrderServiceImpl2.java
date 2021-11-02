package com.baomidou.springboot.services;

import com.baomidou.springboot.entity.TpTradeOrder;
import com.baomidou.springboot.mapper.TradeOrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author linzhenlie-jk
 * @date 2020/12/28
 */
@Service
@Slf4j
public class TradeOrderServiceImpl2 implements TradeOrderService2 {
    @Autowired
    protected TradeOrderMapper mapper;


    @Override
    public void updateTrx(TpTradeOrder order, Boolean throwException) {
        mapper.updateByPrimaryKeySelective(order);
        if (throwException) {
            int i = 1 / 0;
        }
    }
}
