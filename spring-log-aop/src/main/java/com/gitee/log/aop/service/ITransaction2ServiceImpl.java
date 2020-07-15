package com.gitee.log.aop.service;

import com.gitee.log.aop.dao.ProductDao;
import com.gitee.log.aop.entity.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * @author linzhenlie
 * @date 2020-03-31
 */
@Service(value="ITransaction2ServiceImpl")
public class ITransaction2ServiceImpl implements ITransaction2Service {
    @Autowired
    private ProductDao dao;

    //@Transactional(rollbackFor = Exception.class , propagation = Propagation.SUPPORTS)
    @Override
    public void required(Product product){
        Product pro = new Product();
        pro.setName("required2");
        dao.insert(pro);
        int i = 1/0;
    }
}
