package com.baomidou.springboot.services;

import com.alibaba.fastjson.JSON;
import com.baomidou.springboot.entity.TpTradeOrder;
import com.baomidou.springboot.mapper.TradeOrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * @author linzhenlie-jk
 * @date 2020/12/28
 */
@Service
@Slf4j
public class TradeOrderServiceImpl implements TradeOrderService {
    @Autowired
    protected TradeOrderMapper mapper;
    @Autowired
    protected TradeOrderService service;

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
        mapper.updateByPrimaryKeySelective(order);
        int i = 1 / 0;
    }

    @Override
    public TpTradeOrder select(Long id) {
        return mapper.selectById(id);
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

        TpTradeOrder order2 = new TpTradeOrder();
        order2.setId(order.getId());
        order2.setHello("随意咯");
        order2.setCreatedTime(LocalDateTime.now());
        order2.setCreatedBy("创建人");
        order2.setUpdatedTime(LocalDateTime.now());
        order2.setUpdatedBy("修改人");
        // 【事务失效】外层没有事务,this指自己本身不是代理类 ,强转仍然不是代理类
        TradeOrderService service1 = (TradeOrderService) this;
        log.info("==========doBiz5TrxAndThis service1:{}", service1);
        service1.updateTrx(order2);

        // 修改成如下调用，事务生效
        TradeOrderService service2 = (TradeOrderService) AopContext.currentProxy();
        log.info("==========doBiz5TrxAndThis proxy   :{}", service2);
        //service2.updateTrx(order2);
    }

    @Override
    public void doBiz5TrxAndThisTrx() {
        TpTradeOrder order = new TpTradeOrder();
        order.setId(RandomUtils.nextLong());
        // 1 待支付，2支付中，3支付成功，4支付失败，5支付关闭
        order.setTradeStatus("1");
        mapper.insertSelective(order);

        TpTradeOrder order2 = new TpTradeOrder();
        order2.setHello("随意咯Trx");
        order2.setCreatedTime(LocalDateTime.now());
        order2.setCreatedBy("创建人Trx");
        order2.setUpdatedTime(LocalDateTime.now());
        order2.setUpdatedBy("修改人Trx");

        //或者
        TradeOrderService service1 = (TradeOrderService) AopContext.currentProxy();
        log.info("==========doBiz5TrxAndThisTrx service1:{}", service1);
        log.info("==========doBiz5TrxAndThisTrx proxy:{}", AopContext.currentProxy());

        // 外层也【有事务】,this则指proxy代理类本身，事务生效
        TradeOrderService service2 = (TradeOrderService) this;
        log.info("==========doBiz5TrxAndThisTrx service2:{}", service2);
        this.updateTrx(order2);
        //service1.updateTrx(order);
    }
}
