package com.gitee.linzl.mq.producer;

import java.util.UUID;

import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.gitee.linzl.constants.MQConstant;
import com.gitee.linzl.model.User;

/**
 * 延时消息队列,使用RabbitMQ的死信队列,比如消费者没有消费成功,将消息扔回队列等待下次消费
 * 
 * @description
 * @author linzl
 * @email 2225010489@qq.com
 * @date 2018年9月19日
 */
@Component
public class DLXSender {
	@Autowired
	private RabbitTemplate rabbitTemplate;

	/**
	 * 延迟发送消息到队列,死信队列可直接当延迟队列使用
	 * 
	 * @param queue   队列名称
	 * @param message 消息内容
	 * @param times   延迟时间 单位毫秒
	 */
	public void send(String msg) {
		User user = new User();
		user.setId(UUID.randomUUID().toString());
		user.setMsg(msg);

		CorrelationData correlationData = new CorrelationData(user.getId());
		MessagePostProcessor processor = (message) -> {
			message.getMessageProperties().setCorrelationId(correlationData.getId());
			message.getMessageProperties().setExpiration("2000");
			return message;
		};
		rabbitTemplate.convertAndSend(MQConstant.BEFORE_DLX_EXCHANGE, MQConstant.BEFORE_DLX_ROUTE_KEY, user, processor,
				correlationData);
	}
}
