package com.gitee.log.aop.service;

import com.gitee.log.aop.dao.ProductDao;
import com.gitee.log.aop.entity.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.CheckForNull;
import javax.validation.Valid;

/**
 * @author linzhenlie
 * @date 2020-03-31
 */
@Service(value = "ITransactionServiceImpl")
public class ITransactionServiceImpl implements ITransactionService {
    @Autowired
    private ProductDao dao;
    @Autowired
    private ITransaction2Service service;

    @Transactional(rollbackFor = Exception.class , propagation = Propagation.SUPPORTS)
    @Override
    @CheckForNull
    public void required(@NonNull @Valid Product product) {
        Product pro = new Product();
        pro.setName("required");
        dao.insert(pro);
        service.required(pro);

    }
}
