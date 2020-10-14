package com.gitee.linzl.bean.cycle;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @author linzhenlie-jk
 * @date 2020/7/28
 */
@Component
@Slf4j
public class PostConstructTest {
    @PostConstruct
    public void first() {
        log.debug("===============第1个===============");
    }

    @PostConstruct
    public void second() {
        log.debug("===============第2个===============");
    }
}
