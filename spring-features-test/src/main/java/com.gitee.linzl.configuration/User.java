package com.gitee.linzl.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.stereotype.Component;

/**
 * Created by jetty on 18/1/31.
 */
@Component
@Setter
@Getter
public class User implements BeanNameAware {

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