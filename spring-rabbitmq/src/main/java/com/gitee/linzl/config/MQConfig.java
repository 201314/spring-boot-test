package com.gitee.linzl.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.retry.MessageRecoverer;
import org.springframework.amqp.rabbit.retry.RepublishMessageRecoverer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.Objects;

@Configuration
@Slf4j
public class MQConfig {
	@Autowired
	private RabbitTemplate rabbitTemplate;

	@Bean
	public MessageRecoverer messageRecoverer(RabbitTemplate rabbitTemplate) {
		if (log.isDebugEnabled()) {
			log.debug("消息重发");
		}
		return new RepublishMessageRecoverer(rabbitTemplate, "exchangemsxferror", "routingkeymsxferror");
	}

	@PostConstruct
	public void init() {
		log.debug("重新给rabbitTemplate设值");
		// 消息发送失败返回到队列中, yml需要配置 publisher-returns: true
		rabbitTemplate.setMandatory(true);

		/**
		 * 消息确认, yml需要配置 publisher-confirms: true
		 * 
		 * ConfirmCallback接口用于实现消息发送到RabbitMQ交换器后接收ack回调。
		 */
		rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
			if (Objects.isNull(correlationData) || Objects.isNull(correlationData.getId())) {
				return;
			}
			if (ack) {
				// TODO 消息确认发送成功,根据ID更新消息状态为“发送成功”
				if (log.isDebugEnabled()) {
					log.debug("消息确认发送成功ID:【{}】", correlationData.getId());
				}
			} else {
				// TODO 消息确认发送失败,定时服务从消息库定时取出再次发送
				if (log.isDebugEnabled()) {
					log.debug("发送前将消息先入库，如果反馈发送失败，从库中取出再次发送");
					log.debug("消息发送失败:【{}】,重新发送", cause);
				}
			}
		});

		/**
		 * 消息返回, yml需要配置 publisher-returns: true
		 * 
		 * ReturnCallback接口用于实现消息发送到交换器(必须存在)，但无相应队列与交换器绑定时的回调
		 */
		rabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routingKey) -> {
			String correlationId = message.getMessageProperties().getCorrelationId();
			if (log.isDebugEnabled()) {
				log.debug("消息:【{}】 发送失败,ID:【{}】, 应答码：【{}】,原因：【{}】,交换机:【{}】,路由键: 【{}】", new String(message.getBody()),
						correlationId, replyCode, replyText, exchange, routingKey);
			}
		});
	}
}
