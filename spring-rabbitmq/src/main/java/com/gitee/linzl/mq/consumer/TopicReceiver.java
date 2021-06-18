package com.gitee.linzl.mq.consumer;

import java.io.IOException;

import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.gitee.linzl.constants.MQConstant;
import com.rabbitmq.client.Channel;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class TopicReceiver {
	@RabbitListener(bindings = {
			@QueueBinding(value = @Queue(name = MQConstant.TOPIC_QUEUE, durable = "true", autoDelete = "false"),

					exchange = @Exchange(name = MQConstant.TOPIC_EXCHANGE, type = ExchangeTypes.TOPIC),

					key = { MQConstant.TOPIC_ROUTE_KEY }) })
	@RabbitHandler
	public void process(@Payload String msg, Message message, Channel channel) {
		log.debug("topicMessageReceiver:【{}】", msg);
		log.debug("topicMessageReceiver deliveryTag:【{}】", message.getMessageProperties().getDeliveryTag());
		try {
			// 一定要做确认，否则消息会一直存在在队列中
			// 必须使用channel进行消息确认，包括消费成功或消费失败
			channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@RabbitListener(bindings = {
			@QueueBinding(value = @Queue(name = MQConstant.TOPIC_QUEUE_LIKE, durable = "true", autoDelete = "false"),

					exchange = @Exchange(name = MQConstant.TOPIC_EXCHANGE, type = ExchangeTypes.TOPIC),

					key = { MQConstant.TOPIC_ROUTE_KEY_LIKE }) })
	@RabbitHandler
	public void processLike(@Payload String msg, Message message, Channel channel) {
		log.debug("topicMessagesReceiver like===:【{}】", msg);
		log.debug("topicMessagesReceiver like deliveryTag:【{}】", message.getMessageProperties().getDeliveryTag());
		try {
			channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}