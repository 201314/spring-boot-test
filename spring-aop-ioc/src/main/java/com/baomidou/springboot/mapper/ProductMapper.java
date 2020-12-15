package com.baomidou.springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.springboot.entity.Product;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author linzhenlie-jk
 * @date 2020/12/15
 */
@Mapper
public interface ProductMapper extends BaseMapper<Product> {
}
