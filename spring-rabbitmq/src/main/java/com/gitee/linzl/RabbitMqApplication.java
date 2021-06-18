package com.gitee.linzl;

import org.springframework.boot.Banner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class RabbitMqApplication {
	public static void main(String[] args) throws Exception {
		SpringApplicationBuilder sb = new SpringApplicationBuilder(RabbitMqApplication.class);
		sb.bannerMode(Banner.Mode.OFF);
		sb.run(args);
	}
}