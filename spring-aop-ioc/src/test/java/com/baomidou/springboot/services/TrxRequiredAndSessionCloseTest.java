package com.baomidou.springboot.services;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author linzhenlie-jk
 * @date 2021/8/10
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
@ActiveProfiles({"trxRequired", "mybatisSessionClose"})
public class TrxRequiredAndSessionCloseTest {
    @Autowired
    private TradeOrderService service;

    @Test
    public void doBiz2() {
        log.info("doBiz1无事务,事务REQUIRED,mybatis Session一级缓存【关闭】");
        //service.doBiz1NoException();
        service.testTrx();
    }

    @Test
    public void doBiz2Trx() {
        log.info("doBiz1Trx有事务,事务REQUIRED,mybatis Session一级缓存【关闭】");
        service.doBiz1NoExceptionTrx();
    }
}
