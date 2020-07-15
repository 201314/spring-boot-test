package com.gitee.linzl;

import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@Slf4j
public class KafkaBootStrapApplication {

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(KafkaBootStrapApplication.class);
		app.setBannerMode(Banner.Mode.OFF);
		app.run(args);
		log.debug("启动完成");
	}
}
