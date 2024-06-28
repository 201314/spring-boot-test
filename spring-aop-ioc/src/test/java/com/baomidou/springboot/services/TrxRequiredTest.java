package com.baomidou.springboot.services;

import com.fasterxml.jackson.core.JsonProcessingException;
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
@ActiveProfiles({"trxRequired"})
public class TrxRequiredTest {
    @Autowired
    private TradeOrderService service;

    @Test
    public void doBiz1() throws JsonProcessingException {
        log.info("doBiz1无事务,事务REQUIRED");
        service.doBiz1NoException();
        // doBiz1无事务 save成功，updateTrx事务回滚
    }

    @Test
    public void doBiz1Trx() throws JsonProcessingException {
        log.info("doBiz1Trx有事务,事务REQUIRED");
        service.doBiz1NoExceptionTrx();
        // doBiz1Trx有事务 save成功，updateTrx事务回滚, doBiz1Trx事务回滚，全部回滚
    }
}
