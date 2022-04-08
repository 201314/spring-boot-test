package com.baomidou.springboot.services;

import com.baomidou.springboot.entity.TpTradeOrder;
import com.baomidou.springboot.mapper.TradeOrderMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
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
    private TradeOrderMapper mapper;
    @Autowired
    private TradeOrderService2 tradeOrderService2;
    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void saveTrx(Long id) {
        TpTradeOrder order = new TpTradeOrder();
        order.setId(id);
        // 1 待支付，2支付中，3支付成功，4支付失败，5支付关闭
        order.setTradeStatus("1");
        order.setHello("随意1");
        order.setCreatedBy("创建者1");
        order.setUpdatedBy("修改者1");
        mapper.insertSelective(order);
    }


    @Override
    public void updateTrx(TpTradeOrder order, Boolean throwException) {
        mapper.updateByPrimaryKeySelective(order);
        if (throwException) {
            int i = 1 / 0;
        }
    }


    @Override
    public void doBiz1NoException() throws JsonProcessingException {
        Long id = RandomUtils.nextLong();
        saveTrx(id);
        doBiz11(id, Boolean.FALSE);
        doBiz12(id, Boolean.FALSE);
    }

    @Override
    public void doBiz1NoExceptionTrx() throws JsonProcessingException {
        Long id = RandomUtils.nextLong();
        saveTrx(id);
        doBiz11(id, Boolean.FALSE);
    }

    private void doBiz11(Long id, Boolean throwException) throws JsonProcessingException {
        TpTradeOrder order = mapper.selectById(id);
        log.info("第1次为待支付:{}", objectMapper.writeValueAsString(order));
        // 1 待支付，2支付中，3支付成功，4支付失败，5支付关闭
        if (StringUtils.equals("1", order.getTradeStatus())) {
            TpTradeOrder newOrder = new TpTradeOrder();
            newOrder.setId(order.getId());
            newOrder.setTradeStatus("2");
            newOrder.setHello("随意2");
            newOrder.setCreatedBy("创建者2");
            newOrder.setUpdatedBy("修改者2");
            TradeOrderService service1 = (TradeOrderService) AopContext.currentProxy();
            service1.updateTrx(newOrder, throwException);
        }
    }

    private void doBiz12(Long id, Boolean throwException) throws JsonProcessingException {
        TpTradeOrder order = mapper.selectById(id);
        log.info("第2次为支付中:{}", objectMapper.writeValueAsString(order));
        // 1 待支付，2支付中，3支付成功，4支付失败，5支付关闭
        if (StringUtils.equals("2", order.getTradeStatus())) {
            TpTradeOrder newOrder = new TpTradeOrder();
            newOrder.setId(order.getId());
            newOrder.setTradeStatus("3");
            newOrder.setHello("随意3");
            newOrder.setUpdatedBy("修改者");
            TradeOrderService service1 = (TradeOrderService) AopContext.currentProxy();
            service1.updateTrx(newOrder, throwException);
        }
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
        service1.updateTrx(order2, true);

        // 修改成如下调用，事务生效
        TradeOrderService service2 = (TradeOrderService) AopContext.currentProxy();
        log.info("==========doBiz5TrxAndThis proxy   :{}", service2);
        //service2.updateTrx(order2, true);
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
        this.updateTrx(order2, true);
        //service1.updateTrx(order,true);
    }

    @Override
    public void testTrx() {
        // 第一个用this
        Long id = RandomUtils.nextLong();
        this.saveTrx(id);
        // 第二个用Trx，并抛出异常，看第一个this是否有回滚
        TpTradeOrder newOrder = new TpTradeOrder();
        newOrder.setId(id);
        newOrder.setTradeStatus("2");
        newOrder.setHello("随意" + RandomStringUtils.randomNumeric(2));
        newOrder.setCreatedBy("创建者" + RandomStringUtils.randomNumeric(2));
        newOrder.setUpdatedBy("修改者" + RandomStringUtils.randomNumeric(2));
        tradeOrderService2.updateTrx(newOrder, true);
    }
}
