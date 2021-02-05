package com.baomidou.springboot.services;

import com.gitee.linzl.commons.tools.SpringContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author linzhenlie-jk
 * @date 2020/12/15
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class ForEachTest {
    @Autowired
    private List<ForEachService> service;

    @Test
    public void test() throws InterruptedException {
        System.out.println(service);
        ExecutorService executor = Executors.newFixedThreadPool(2);
        System.out.println(service.getClass());
        executor.submit(() -> {
            service.stream().forEach(service -> {
                service.print();
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        });
        executor.submit(() -> {
            service.stream().forEach(service -> {
                service.print();
            });
        });
        System.out.println(service);
        TimeUnit.SECONDS.sleep(2);
    }

    @Test
    public void test2() {
        List<ForEachService> list = SpringContextUtil.getBeansList(ForEachService.class);
        System.out.println("service:" + service);
        System.out.println("list:" + list);
    }
}
