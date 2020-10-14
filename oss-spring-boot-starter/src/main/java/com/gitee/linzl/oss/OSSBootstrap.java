package com.gitee.linzl.oss;

import com.gitee.linzl.EnableAutoCommons;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

@EnableAutoCommons
public class OSSBootstrap {
	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(OSSBootstrap.class);
		app.setBannerMode(Banner.Mode.OFF);
		ConfigurableApplicationContext context = app.run(args);
		for (String beanDefinitionName : context.getBeanDefinitionNames()) {
			System.out.println(beanDefinitionName + "==>" + context.getBean(beanDefinitionName));
		}
	}
}