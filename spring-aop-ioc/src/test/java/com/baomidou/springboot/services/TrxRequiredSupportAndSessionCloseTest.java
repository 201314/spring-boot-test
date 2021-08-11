package com.baomidou.springboot.services;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
@ActiveProfiles({"trxAndSupport", "mybatisSessionClose"})
public class TrxRequiredSupportAndSessionCloseTest {
    @Autowired
    private TradeOrderService service;

    @Test
    public void doBiz4() {
        log.info("doBiz1无事务,事务REQUIRED、SUPPORT,mybatis Session一级缓存【关闭】");
        service.doBiz1NoException();
    }

    @Test
    public void doBiz4Trx() {
        log.info("doBiz1Trx有事务,事务REQUIRED、SUPPORT,mybatis Session一级缓存【关闭】");
        service.doBiz1NoExceptionTrx();
    }
}
