package com.baomidou.springboot.services;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author linzhenlie-jk
 * @date 2020/12/15
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class TradeOrderTest {
    @Autowired
    private TradeOrderService service;

    @Test
    public void save() {
        service.save(null);
    }

    @Test
    public void doBiz1() {
        log.info("doBiz1");
        service.doBiz1();
    }

    @Test
    public void doBiz1Trx() {
        log.info("doBiz1Trx");
        service.doBiz1Trx();
    }

    @Test
    public void doBiz2() {
        log.info("doBiz2");
        service.doBiz2();
    }

    @Test
    public void doBiz2Trx() {
        log.info("doBiz2Trx");
        service.doBiz2Trx();
    }

    @Test
    public void doBiz3() {
        log.info("doBiz3");
        service.doBiz3();
    }

    @Test
    public void doBiz3Trx() {
        log.info("doBiz3Trx");
        service.doBiz3Trx();
    }

    @Test
    public void doBiz4() {
        log.info("doBiz4");
        service.doBiz4();
    }

    @Test
    public void doBiz4Trx() {
        log.info("doBiz4Trx");
        service.doBiz4Trx();
    }
}
