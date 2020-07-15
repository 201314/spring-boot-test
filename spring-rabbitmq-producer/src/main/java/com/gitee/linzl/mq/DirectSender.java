package com.gitee.linzl.mq;

import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.gitee.linzl.constants.MQConstant;
import com.gitee.linzl.model.User;

/**
 * 创建普通消息队列 ，点对点模式
 * 
 * @description
 * 
 * @author linzl
 * @email 2225010489@qq.com
 * @date 2018年9月19日
 */
@Component
public class DirectSender {
	@Autowired
	private RabbitTemplate rabbitTemplate;

	public void send(User user) {
		CorrelationData correlationData = new CorrelationData(user.getId());
		MessagePostProcessor processor = (message) -> {
			message.getMessageProperties().setCorrelationId(correlationData.getId());
			return message;
		};

		// TODO 发送前将ID和消息关系存储起来,状态为“待发送”
		this.rabbitTemplate.convertAndSend(MQConstant.SMS_EXCHANGE, MQConstant.SMS_ROUTE_KEY, user, processor,
				correlationData);
	}
}