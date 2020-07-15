package com.gitee.log.aop;

import com.gitee.linzl.EnableAutoCommons;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
@EnableAutoCommons
public class LogAopApplication {

    public static void main(String[] args) {
        SpringApplication.run(LogAopApplication.class, args);
    }

}
