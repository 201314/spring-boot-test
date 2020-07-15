package com.gitee.linzl.ftp;

import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FtpBootstrap {
	public static void main(String args[]) {
		SpringApplication app = new SpringApplication(FtpBootstrap.class);
		app.setBannerMode(Banner.Mode.OFF);
		app.run(args);
	}
}
