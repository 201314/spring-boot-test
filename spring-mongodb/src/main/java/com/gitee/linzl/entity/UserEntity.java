package com.gitee.linzl.entity;

import com.alibaba.fastjson.JSON;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Date;

/**
 * @author linzhenlie
 */
@Getter
@Setter
@RequiredArgsConstructor
@Document(collection = "user_test")
//@CompoundIndex(name = "mix_index", def = "{id : 1, name : 1}")
public class UserEntity implements Serializable {
    private static final long serialVersionUID = -3258839839160856613L;
    //@Id
    private Long id;
    private String userName;
    private String passWord;
    private Date birthday;

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}