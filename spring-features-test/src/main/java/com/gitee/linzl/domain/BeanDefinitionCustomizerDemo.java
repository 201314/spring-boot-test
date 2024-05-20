package com.gitee.linzl.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;

/**
 * @author linzhenlie-jk
 * @date 2023/7/25
 */
@Setter
@Getter
public class BeanDefinitionCustomizerDemo {

    @Value(value = "1234")
    private String beanDefineTest;
}
