package com.baomidou.springboot.mapper;

import com.baomidou.springboot.entity.TpTradeOrder;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author linzhenlie-jk
 * @date 2020/12/15
 */
@Mapper
public interface TradeOrderMapper {
    TpTradeOrder selectById(Long id);

    int insertSelective(TpTradeOrder record);

    int updateByPrimaryKeySelective(TpTradeOrder record);

    int updateByPrimaryKey(TpTradeOrder record);
}
