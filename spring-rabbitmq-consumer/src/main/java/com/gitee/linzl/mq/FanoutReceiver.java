package com.gitee.linzl.mq;

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
public class FanoutReceiver {
	@RabbitListener(bindings = {
			@QueueBinding(value = @Queue(name = MQConstant.FANOUT_QUEUE_0, durable = "true", autoDelete = "false"),

					exchange = @Exchange(name = MQConstant.FANOUT_EXCHANGE, type = ExchangeTypes.FANOUT),

					key = { MQConstant.FANOUT_ROUTE_KEY }) })
	@RabbitHandler
	public void processA(@Payload String msg, Message message, Channel channel) {
		if (log.isDebugEnabled()) {
			log.debug("FanoutReceiverA:【{}】,FanoutReceiverA deliveryTag:【{}】 ", msg,
					message.getMessageProperties().getDeliveryTag());
		}
		try {
			// 一定要做确认，否则消息会一直存在在队列中
			// 必须使用channel进行消息确认，包括消费成功或消费失败
			channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@RabbitListener(bindings = {
			@QueueBinding(value = @Queue(name = MQConstant.FANOUT_QUEUE_1, durable = "true", autoDelete = "false"),

					exchange = @Exchange(name = MQConstant.FANOUT_EXCHANGE, type = ExchangeTypes.FANOUT),

					key = { MQConstant.FANOUT_ROUTE_KEY }) })
	@RabbitHandler
	public void processB(@Payload String msg, Message message, Channel channel) {
		if (log.isDebugEnabled()) {
			log.debug("FanoutReceiverB:【{}】,FanoutReceiverB deliveryTag:【{}】 ", msg,
					message.getMessageProperties().getDeliveryTag());
		}
		try {
			channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@RabbitListener(bindings = {
			@QueueBinding(value = @Queue(name = MQConstant.FANOUT_QUEUE_2, durable = "true", autoDelete = "false"),

					exchange = @Exchange(name = MQConstant.FANOUT_EXCHANGE, type = ExchangeTypes.FANOUT),

					key = { MQConstant.FANOUT_ROUTE_KEY }) })
	@RabbitHandler
	public void processC(@Payload String msg, Message message, Channel channel) {
		if (log.isDebugEnabled()) {
			log.debug("FanoutReceiverC:【{}】,FanoutReceiverC deliveryTag:【{}】", msg,
					message.getMessageProperties().getDeliveryTag());
		}
		try {
			channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}