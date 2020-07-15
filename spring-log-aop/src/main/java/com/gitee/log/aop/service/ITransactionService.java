package com.gitee.log.aop.service;

import com.gitee.log.aop.entity.Product;

/**
 * @author linzhenlie
 * @date 2020-03-31
 */

public interface ITransactionService {

    void required(Product product);

    //void requiredNew(Product product);
}
