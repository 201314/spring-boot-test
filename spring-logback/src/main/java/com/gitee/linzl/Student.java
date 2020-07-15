package com.gitee.linzl;

import com.alibaba.fastjson.JSON;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Student {
    private String fileName;
    private Integer age;

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
