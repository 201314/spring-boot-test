package com.gitee.linzl;

import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WebSocketBootstrap {

	public static void main(String args[]) {
		SpringApplication app = new SpringApplication(WebSocketBootstrap.class);
		app.setBannerMode(Banner.Mode.OFF);
		app.run(args);
	}

}