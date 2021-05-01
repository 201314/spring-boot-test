package com.baomidou.springboot.services;

import com.alibaba.fastjson.JSON;
import com.baomidou.springboot.entity.TpTradeOrder;
import com.baomidou.springboot.mapper.TradeOrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

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
    public void save(TpTradeOrder order) {
        order = new TpTradeOrder();
        order.setId(11223344L);
        // 1 待支付，2支付中，3支付成功，4支付失败，5支付关闭
        order.setTradeStatus("1");
        mapper.insertSelective(order);
    }

    @Override
    public void updateTrx(TpTradeOrder order) {
        //TradeOrderService service1 = (TradeOrderService) this;
        mapper.updateByPrimaryKeySelective(order);
        int i = 1 / 0;
    }

    @Override
    public TpTradeOrder select(Long id) {
        //return mapper.selectById(id);
        return null;
    }

    @Override
    public void doBiz11() {
        TpTradeOrder order = service.select(11223344L);
        log.info("==========doBiz11 service01:{}", service);
        log.info("第1次为待支付:{}", JSON.toJSONString(order));
        // 1 待支付，2支付中，3支付成功，4支付失败，5支付关闭
        if ("1".equals(order.getTradeStatus())) {
            TpTradeOrder newOrder = new TpTradeOrder();
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
        TpTradeOrder order = service.select(11223344L);
        log.info("==========doBiz12 service01:{}", service);
        log.info("第2次为支付中:{}", JSON.toJSONString(order));
        // 1 待支付，2支付中，3支付成功，4支付失败，5支付关闭
        if ("2".equals(order.getTradeStatus())) {
            TpTradeOrder newOrder = new TpTradeOrder();
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

    /**
     * doBiz5TrxAndThis 没有设置事务，updateTrx 设置了事务，doBiz5TrxAndThis 调用 updateTrx 时，updateTrx 事务失效
     */
    @Override
    public void doBiz5TrxAndThis() {
        TpTradeOrder order = new TpTradeOrder();
        order.setId(RandomUtils.nextLong());
        // 1 待支付，2支付中，3支付成功，4支付失败，5支付关闭
        order.setTradeStatus("1");
        mapper.insertSelective(order);

        order.setHello("随意咯");
        order.setCreatedTime(LocalDateTime.now());
        order.setCreatedBy("创建人");
        order.setUpdatedTime(LocalDateTime.now());
        order.setUpdatedBy("修改人");
        // 事务失效,外层也没有事务,this是用的自己本身不是代理类 ,强转仍然不是代理类
        TradeOrderService service1 = (TradeOrderService) this;
        log.info("==========doBiz5TrxAndThis service1:{}", service1);
        service1.updateTrx(order);
    }

    @Override
    @Transactional
    public void doBiz5TrxAndThisTrx() {
        TradeOrderService service2 = (TradeOrderService) AopContext.currentProxy();
        System.out.println("service2====>"+service2);
        TpTradeOrder order = new TpTradeOrder();
        order.setId(RandomUtils.nextLong());
        // 1 待支付，2支付中，3支付成功，4支付失败，5支付关闭
        order.setTradeStatus("1");
        mapper.insertSelective(order);

        order.setHello("随意咯Trx");
        order.setCreatedTime(LocalDateTime.now());
        order.setCreatedBy("创建人Trx");
        order.setUpdatedTime(LocalDateTime.now());
        order.setUpdatedBy("修改人Trx");

        // 事务失效,外层也【有事务】,this代理类本身
        TradeOrderService service1 = (TradeOrderService) this;
        log.info("==========doBiz5TrxAndThisTrx service1:{}", service1);
        service1.updateTrx(order);
        //或者
        //TradeOrderService service1 = (TradeOrderService) AopContext.currentProxy();
        //service1.updateTrx(order);
    }
}
