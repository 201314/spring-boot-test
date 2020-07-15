package com.gitee.log.aop.rest;

import com.gitee.linzl.commons.annotation.RequiredPermissionToken;
import com.gitee.linzl.commons.api.ApiResult;
import com.gitee.log.aop.dao.ProductDao;
import com.gitee.log.aop.entity.Product;
import com.gitee.log.aop.service.ITransactionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

@Slf4j
@RestController
@RequestMapping(value = "/user")
@RequiredPermissionToken
public class ProductRest {
    @Autowired
    private ProductDao productDao;

    @GetMapping("add")
    public String add() {
        Product product = new Product();
        product.setName("dell computer");
        product.setOnlineTime(new Date());
        product.setBuyPrice(new BigDecimal("29.5"));
        product.setCategory("computer");
        product.setDetail("this is a dell notebook");
        product.setUpdateTime(new Date());
        int count = productDao.insert(product);
        log.debug("new product id:" + product.getId());
        return "添加成功";
    }

    @GetMapping("update")
    public String testUpdate() {
        Product product = productDao.selectById(1L);
        product.setName("test-update");
        product.setBuyPrice(new BigDecimal("23.5"));
        product.setOnlineTime(new Date());
        productDao.updateById(product);
        return "修改成功";
    }
    @Autowired
    private ITransactionService service;
    @GetMapping("test")
    public ApiResult test() {
        Product product = new Product();
        product.setUpdateTime(new Date());
        ApiResult api = ApiResult.success();
        api.setRequestTime(LocalDateTime.now());
        api.setData(product);
        return api;
    }
}
