package com.gitee.linzl;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.retry.MessageRecoverer;
import org.springframework.amqp.rabbit.retry.RepublishMessageRecoverer;
import org.springframework.boot.Banner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import lombok.extern.slf4j.Slf4j;

@EnableTransactionManagement
@SpringBootApplication
@Slf4j
public class ConsumerApplication {
	public static void main(String[] args) throws Exception {
		SpringApplicationBuilder sb = new SpringApplicationBuilder(ConsumerApplication.class);
		sb.bannerMode(Banner.Mode.OFF);
		sb.run(args);
	}

	@Bean
	public MessageRecoverer messageRecoverer(RabbitTemplate rabbitTemplate) {
		if (log.isDebugEnabled()) {
			log.debug("消息重发");
		}
		return new RepublishMessageRecoverer(rabbitTemplate, "exchangemsxferror", "routingkeymsxferror");
	}
}