package com.baomidou.springboot.mapper;

import com.baomidou.springboot.entity.TradeOrder;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author linzhenlie-jk
 * @date 2020/12/15
 */
@Mapper
public interface TradeOrderMapper {
    /**
     *
     * @mbg.generated 2021-01-08 18:02:50
     */
    int deleteByPrimaryKey(Long id);

    /**
     *
     * @mbg.generated 2021-01-08 18:02:50
     */
    int insert(TradeOrder record);

    /**
     *
     * @mbg.generated 2021-01-08 18:02:50
     */
    int insertSelective(TradeOrder record);

    /**
     *
     * @mbg.generated 2021-01-08 18:02:50
     */
    int updateByPrimaryKeySelective(TradeOrder record);

    /**
     *
     * @mbg.generated 2021-01-08 18:02:50
     */
    int updateByPrimaryKey(TradeOrder record);
}
