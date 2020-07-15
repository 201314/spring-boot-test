package com.gitee.log.aop;

import java.math.BigDecimal;
import java.util.Date;

import com.gitee.log.aop.service.ITransaction2Service;
import com.gitee.log.aop.service.ITransactionService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.gitee.log.aop.dao.ProductDao;
import com.gitee.log.aop.entity.Product;

import lombok.extern.slf4j.Slf4j;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class LogAopApplicationTests {

	@Autowired
	private ProductDao productDao;

	@Test
	public void testInsert() {
		Product product = new Product();
		product.setName("dell computer");
		product.setOnlineTime(new Date());
		product.setBuyPrice(new BigDecimal("29.5"));
		product.setCategory("computer");
		product.setDetail("this is a dell notebook");
		product.setUpdateTime(new Date());
		int count = productDao.insert(product);
		log.debug("new product id:" + product.getId());
	}

	@Test
	public void testUpdate() {
		Product product = productDao.selectById(1L);
		product.setName("test-update");
		product.setBuyPrice(new BigDecimal("23.5"));
		product.setOnlineTime(new Date());
		productDao.updateById(product);
	}

	@Test
	public void testDelete() {
		productDao.deleteById(1L);
	}

	@Autowired
	private ITransactionService service;
	@Test
	public void testTransaction(){
		// A
		service.required(null);
	}
}
