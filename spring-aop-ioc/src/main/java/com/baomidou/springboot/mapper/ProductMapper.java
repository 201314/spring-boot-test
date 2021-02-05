package com.baomidou.springboot.mapper;

import com.baomidou.springboot.entity.Product;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author linzhenlie-jk
 * @date 2020/12/15
 */
@Mapper
public interface ProductMapper {
    /**
     *
     * @mbg.generated 2021-01-08 18:02:50
     */
    int deleteByPrimaryKey(Long id);

    /**
     *
     * @mbg.generated 2021-01-08 18:02:50
     */
    int insert(Product record);

    /**
     *
     * @mbg.generated 2021-01-08 18:02:50
     */
    int insertSelective(Product record);

    /**
     *
     * @mbg.generated 2021-01-08 18:02:50
     */
    int updateByPrimaryKeySelective(Product record);

    /**
     *
     * @mbg.generated 2021-01-08 18:02:50
     */
    int updateByPrimaryKey(Product record);
}
