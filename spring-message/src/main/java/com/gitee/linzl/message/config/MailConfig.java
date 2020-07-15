package com.gitee.linzl.message.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
public class MailConfig {
	// 通过阿波罗配置
	@Bean("mailSender")
	@ConfigurationProperties(prefix = "mail.config")
	public JavaMailSender getMailSender() {
		JavaMailSenderImpl config = new JavaMailSenderImpl();
		return config;
	}

	// 通过阿波罗配置
	@Bean("simpleMailMessage")
	@ConfigurationProperties(prefix = "mail.config")
	public SimpleMailMessage getMailMessage() {
		SimpleMailMessage config = new SimpleMailMessage();
		return config;
	}

}
