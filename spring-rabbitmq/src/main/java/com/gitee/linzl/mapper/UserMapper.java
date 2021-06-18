package com.gitee.linzl.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gitee.linzl.model.User;

@Mapper
public interface UserMapper extends BaseMapper<User> {

}
