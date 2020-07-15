package com.gitee.linzl.message.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.gitee.linzl.message.service.SendSmsService;

@Configuration
public class SmsConfig {
	@Bean
	@ConfigurationProperties(prefix = "dongxin.config")
	public SendSmsService getSmsService() {
		SendSmsService config = new SendSmsService();
		return config;
	}
}
