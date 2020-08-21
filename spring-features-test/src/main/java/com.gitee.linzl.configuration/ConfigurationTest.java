package com.gitee.linzl.configuration;

import lombok.Getter;
import lombok.Setter;

/**
 * @author linzhenlie-jk
 * @date 2020/8/3
 */
@Setter
@Getter
public class ConfigurationTest {
    private String name;

    private String address;

    private ConfigurationTest2 test2;
}
