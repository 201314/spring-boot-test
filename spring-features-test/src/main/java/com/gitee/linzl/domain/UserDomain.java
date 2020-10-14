package com.gitee.linzl.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.stereotype.Component;

@Component
@Setter
@Getter
public class UserDomain implements BeanNameAware {

    private String id;

    private String name;

    private String address;

    @Override
    public void setBeanName(String beanName) {
        //ID保存BeanName的值
        id = beanName;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}