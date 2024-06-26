package com.baomidou.springboot.services;

import com.fasterxml.jackson.core.JsonProcessingException;
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
@ActiveProfiles({"trxAndSupport"})
public class TrxSupportTest {
    @Autowired
    private TradeOrderService service;

    @Test
    public void doBiz3() throws JsonProcessingException {
        log.info("doBiz1无事务,事务REQUIRED、SUPPORT");
        service.doBiz1NoException();
    }

    @Test
    public void doBiz3Trx() throws JsonProcessingException {
        log.info("doBiz1Trx有事务,事务REQUIRED、SUPPORT");
        service.doBiz1NoExceptionTrx();
    }

    @Test
    public void doBiz5TrxAndThis() {
        log.info("doBiz5TrxAndThis");
        service.doBiz5TrxAndThis();
    }

    @Test
    public void doBiz5TrxAndThisTrx() {
        log.info("doBiz5TrxAndThisTrx");
        service.doBiz5TrxAndThisTrx();
    }
}
