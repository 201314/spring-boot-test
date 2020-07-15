package com.linzl.elasticsearch;

import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ElasticSearchBootstrap {

	public static void main(String args[]) {
		SpringApplication app = new SpringApplication(ElasticSearchBootstrap.class);
		app.setBannerMode(Banner.Mode.OFF);
		app.run(args);
	}

}