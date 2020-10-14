package com.gitee.linzl.domain;

import lombok.Getter;
import lombok.Setter;

/**
 * @author linzhenlie-jk
 * @date 2020/8/3
 */
@Setter
@Getter
public class ConfigurationDomain {
    private String name;

    private String address;

    private ConfigurationDomain2 test2;
}
