package com.gitee.linzl.mq;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.gitee.linzl.constants.MQConstant;

/**
 * 创建普通消息队列,发布订阅的模式,只要符合路由键,就会收到消息
 * 
 * 由结果可知:
 * <p>
 * sender1发送的消息,routing_key是“topic.message”,</br>
 * 所以exchange里面的绑定的binding_key是“topic.message”, “topic.＃”都符合路由规则;</br>
 * 所以sender1 发送的消息，两个队列都能接收到;
 * <p>
 * sender2发送的消息,routing_key是“topic.messages”,</br>
 * 所以exchange里面的绑定的binding_key只有“topic.＃”都符合路由规则;</br>
 * 所以sender2发送的消息只有队列“topic.messages”能收到。
 * 
 * @author linzl
 * @email 2225010489@qq.com
 * @date 2018年2月10日
 */
@Component
public class TopicSender {
	@Autowired
	private RabbitTemplate rabbitTemplate;

	public void send(String msg) {
//		this.rabbitTemplate.convertAndSend(MQConstant.TOPIC_EXCHANGE, MQConstant.TOPIC_ROUTE_KEY, msg);
		this.rabbitTemplate.convertAndSend(MQConstant.TOPIC_EXCHANGE, MQConstant.TOPIC_ROUTE_KEY_LIKE, msg);
	}
}