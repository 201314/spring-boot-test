package com.gitee.log.aop.datalog;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.gitee.log.aop.entity.Operation;

public interface OperationLogDao extends MongoRepository<Operation,String> {
}
