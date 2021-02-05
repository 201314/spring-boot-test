package com.baomidou.springboot.services;

import com.alibaba.fastjson.JSON;
import com.baomidou.springboot.entity.TradeOrder;
import com.baomidou.springboot.mapper.TradeOrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author linzhenlie-jk
 * @date 2020/12/28
 */
@Service
@Slf4j
public class TradeOrderServiceImpl implements TradeOrderService {
    @Autowired
    private TradeOrderMapper mapper;
    @Autowired
    private TradeOrderService service;

    @Override
    public void save(TradeOrder order) {
        order = new TradeOrder();
        order.setId(11223344L);
        // 1 待支付，2支付中，3支付成功，4支付失败，5支付关闭
        order.setTradeStatus("1");
        mapper.insert(order);
    }

    @Override
    public void updateTrx(TradeOrder order) {
        TradeOrderService service1 = (TradeOrderService) this;
        //mapper.updateById(order);
        int i = 1 / 0;
    }

    @Override
    public TradeOrder select(Long id) {
        //return mapper.selectById(id);
        return null;
    }

    @Override
    public void doBiz11() {
        TradeOrder order = service.select(11223344L);
        log.info("==========doBiz11 service01:{}", service);
        log.info("第1次为待支付:{}", JSON.toJSONString(order));
        // 1 待支付，2支付中，3支付成功，4支付失败，5支付关闭
        if ("1".equals(order.getTradeStatus())) {
            TradeOrder newOrder = new TradeOrder();
            newOrder.setId(order.getId());
            newOrder.setTradeStatus("2");
            newOrder.setHello("随意2");
            log.info("==========doBiz11 service02:{}", service);
            TradeOrderService service1 = (TradeOrderService) AopContext.currentProxy();
            service1.updateTrx(newOrder);
            log.info("==========doBiz11 service03:{}", AopContext.currentProxy());
        }
    }

    @Override
    public void doBiz12() {
        TradeOrder order = service.select(11223344L);
        log.info("==========doBiz12 service01:{}", service);
        log.info("第2次为支付中:{}", JSON.toJSONString(order));
        // 1 待支付，2支付中，3支付成功，4支付失败，5支付关闭
        if ("2".equals(order.getTradeStatus())) {
            TradeOrder newOrder = new TradeOrder();
            newOrder.setId(order.getId());
            newOrder.setTradeStatus("3");
            newOrder.setHello("随意3");
            log.info("==========doBiz12 service:{}", service);
            TradeOrderService service1 = (TradeOrderService) this;
            service1.updateTrx(newOrder);
            log.info("==========doBiz11 service03:{}", AopContext.currentProxy());
        }
    }

    @Override
    public void doBiz1() {
        service.doBiz11();
        service.doBiz12();
    }

    @Override
    public void doBiz1Trx() {
        service.doBiz11();
        service.doBiz12();
    }

    @Override
    public void doBiz2() {
        service.doBiz11();
        service.doBiz12();
    }

    @Override
    public void doBiz2Trx() {
        service.doBiz11();
        service.doBiz12();
    }

    @Override
    public void doBiz3() {
        service.doBiz11();
        service.doBiz12();
    }

    @Override
    public void doBiz3Trx() {
        service.doBiz11();
        service.doBiz12();
    }

    @Override
    public void doBiz4() {
        service.doBiz11();
        service.doBiz12();
    }

    @Override
    public void doBiz4Trx() {
        service.doBiz11();
        service.doBiz12();
    }
}
