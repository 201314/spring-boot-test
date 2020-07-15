package com.gitee.log.aop.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gitee.log.aop.entity.Product;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author linzhenlie
 */
@Mapper
public interface ProductDao extends BaseMapper<Product> {
}
