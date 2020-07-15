package com.gitee.linzl.mq;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.gitee.linzl.constants.MQConstant;

/**
 * 创建普通消息队列,广播模式 Fanout Exchange
 * 
 * @description
 * @author linzl
 * @email 2225010489@qq.com
 * @date 2018年9月19日
 */
@Component
public class FanoutSender {
	@Autowired
	private RabbitTemplate rabbitTemplate;

	/**
	 * 就算fanoutSender发送消息的时候，指定了routing_key为"abcd.ee"，但是所有接收者都接受到了消息,
	 * 
	 * 所以routing_key可以为“”
	 */
	public void send(String msg) {
		this.rabbitTemplate.convertAndSend(MQConstant.FANOUT_EXCHANGE, MQConstant.FANOUT_ROUTE_KEY, msg);
	}
}