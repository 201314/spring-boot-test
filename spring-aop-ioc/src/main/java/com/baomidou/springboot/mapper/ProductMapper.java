package com.baomidou.springboot.mapper;

import com.baomidou.springboot.entity.Product;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author linzhenlie-jk
 * @date 2020/12/15
 */
@Mapper
public interface ProductMapper {
    int deleteByPrimaryKey(Long id);

    int insert(Product record);

    int insertSelective(Product record);

    int updateByPrimaryKeySelective(Product record);

    int updateByPrimaryKey(Product record);
}
