package com.baomidou.springboot.services;

import com.baomidou.springboot.Test;
import com.baomidou.springboot.entity.Product;
import com.baomidou.springboot.entity.TpTradeOrder;
import com.baomidou.springboot.mapper.ProductMapper;
import com.baomidou.springboot.mapper.TradeOrderMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gitee.linzl.commons.annotation.RpcExceptionCatch;
import com.gitee.linzl.commons.api.ApiResult;
import com.gitee.linzl.commons.enums.BaseErrorCode;
import com.gitee.linzl.commons.exception.BusinessException;
import com.gitee.linzl.commons.tools.ApiResults;
import com.gitee.linzl.datasource.DynamicDataSourceHolder;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j

public class MyService implements IMyService {
    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private TradeOrderMapper tradeOrderMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    @Test
    public void add() {
        Product product = new Product();
        product.setName("我是写库：" + RandomStringUtils.randomAlphabetic(10));
        productMapper.insertSelective(product);
    }

    @Override
    @Test
    public void select1() {
        Long id = RandomUtils.nextLong();
        TpTradeOrder order = new TpTradeOrder();
        order.setId(id);
        // 1 待支付，2支付中，3支付成功，4支付失败，5支付关闭
        order.setTradeStatus("1");
        order.setHello("select1:随意1");
        order.setCreatedBy("创建者" + RandomStringUtils.randomNumeric(10));
        order.setUpdatedBy("修改者1");
        tradeOrderMapper.insertSelective(order);

        TpTradeOrder order2 = tradeOrderMapper.selectById(id);
    }

    @Override
    @Test
    public void select2() {
        Long id = RandomUtils.nextLong();
        TpTradeOrder order = new TpTradeOrder();
        order.setId(id);
        // 1 待支付，2支付中，3支付成功，4支付失败，5支付关闭
        order.setTradeStatus("1");
        order.setHello("select2:随意1");
        order.setCreatedBy("创建者" + RandomStringUtils.randomNumeric(10));
        order.setUpdatedBy("修改者1");
        tradeOrderMapper.insertSelective(order);

        TpTradeOrder order2 = tradeOrderMapper.selectById(123456L);
    }

    @Override
    public void doSomeThing(String someThing) {
        log.debug("执行被拦截的方法：" + someThing);
    }

    @Override
    @RpcExceptionCatch
    public ApiResult doSomeThing2(String someThing) {
        log.debug("doSomeThing2：" + someThing);
        throw new BusinessException(BaseErrorCode.SERVICE_NOT_AVAILABLE);
    }

    @Override
    public void doSomeThing3(String someThing) {
        log.debug("doSomeThing3：" + someThing);
        throw new BusinessException(BaseErrorCode.SYS_ERROR);
    }

    @Override
    public ApiResult doSomeThing4(String someThing) {
        log.debug("doSomeThing4,没有异常@RpcExceptionCatch注解：" + someThing);
        throw new BusinessException(BaseErrorCode.SERVICE_NOT_AVAILABLE);
    }

    @RpcExceptionCatch
    @Override
    public ApiResult doSomeThing5(String someThing) {
        log.debug("doSomeThing4,没有异常@RpcExceptionCatch注解：" + someThing);
        int zero = 1 / 0;
        return ApiResults.success();
    }
}
