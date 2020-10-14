package com.gitee.linzl.configuration;

import com.gitee.linzl.domain.ComponentDomain;
import com.gitee.linzl.domain.ComponentDomain2;
import com.gitee.linzl.domain.ConfigurationDomain;
import com.gitee.linzl.domain.ConfigurationDomain2;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TestApplicationTests {

    @Autowired
    private ConfigurationDomain car;

    @Autowired
    private ConfigurationDomain2 driver;

    @Test
    public void contextLoads() {
        boolean result = car.getTest2() == driver;
        System.out.println(result ? "同一个car" : "不同的car");
    }

    @Autowired
    private ComponentDomain test;

    @Autowired
    private ComponentDomain2 test2;

    @Test
    public void contextLoads2() {
        boolean result = test.getTest2() == test2;
        System.out.println(result ? "同一个car" : "不同的car");
    }
}