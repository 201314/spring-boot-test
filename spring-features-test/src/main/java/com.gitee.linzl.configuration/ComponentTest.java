package com.gitee.linzl.configuration;

import lombok.Getter;
import lombok.Setter;

/**
 * @author linzhenlie-jk
 * @date 2020/8/3
 */
@Setter
@Getter
public class ComponentTest {
    private String name;

    private String address;

    private ComponentTest2 test2;
}
