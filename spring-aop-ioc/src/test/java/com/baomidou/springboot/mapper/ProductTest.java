package com.baomidou.springboot.mapper;

import com.baomidou.springboot.entity.Product;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;

/**
 * @author linzhenlie-jk
 * @date 2020/12/15
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ProductTest {
    @Autowired
    private ProductMapper mapper;

    @Test
    public void testInsert() {
        Product product = new Product();
        product.setName("加密字段");
        product.setCategory("服装类目");
        product.setDetail("关注细节");
        product.setBuyPrice(new BigDecimal("112.01"));
        mapper.insert(product);
    }
}
