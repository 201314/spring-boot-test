package com.gitee.linzl.mq.consumer;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Argument;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.gitee.linzl.constants.MQConstant;
import com.gitee.linzl.model.User;
import com.rabbitmq.client.Channel;

import lombok.extern.slf4j.Slf4j;

/**
 * 进入死信队列的条件如下:
 * 
 * 消息被拒绝(basic.reject/ basic.nack),并且不再重新投递 requeue=false
 * 
 * 消息的过期时间到期了(rabbitmq Time-To-Live -> messageProperties.setExpiration())
 * 
 * 队列超载
 * 
 * @author victor
 * @desc 死信接收处理消费者
 */
@Component
@Slf4j
public class DLXReceiver {
	@RabbitListener(bindings = {
			@QueueBinding(value = @Queue(name = MQConstant.BEFORE_DLX_QUEUE, durable = "true", autoDelete = "false",

					arguments = { // 死信交换机
							@Argument(name = "x-dead-letter-exchange", value = MQConstant.DLX_EXCHANGE),
							// 死信路由键
							@Argument(name = "x-dead-letter-routing-key", value = MQConstant.DLX_ROUTE_KEY),
							// 队列消息过期时间
							@Argument(name = "x-message-ttl", value = "3000", type = "java.lang.Long") }),

					exchange = @Exchange(name = MQConstant.BEFORE_DLX_EXCHANGE),

					key = { MQConstant.BEFORE_DLX_ROUTE_KEY }) })
	@RabbitHandler
	public void process(@Payload User user, Message message, Channel channel) throws Exception {
		if (log.isDebugEnabled()) {
			log.debug("dlx接收==》【{}】", JSON.toJSONString(user));
		}
		try {
			int i = 1 / 0;
		} catch (Exception e) {
			log.error("我失败了");
			// 消息被拒绝,进入死信队列,死信队列也可做为延迟队列使用
			// 一定要做确认，否则消息会一直存在在队列中
			// 必须使用channel进行消息确认，包括消费成功或消费失败
			channel.basicReject(message.getMessageProperties().getDeliveryTag(), false);
		}
		Thread.sleep(5000);
	}

	@RabbitListener(bindings = {
			@QueueBinding(value = @Queue(name = MQConstant.DLX_QUEUE, durable = "true", autoDelete = "false"),

					exchange = @Exchange(name = MQConstant.DLX_EXCHANGE),

					key = { MQConstant.DLX_ROUTE_KEY }) })
	@RabbitHandler
	public void process2(@Payload User user, Message message, Channel channel) throws Exception {
		log.debug("处理死信队列dlx接收==》【{}】", JSON.toJSONString(user));
		channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
	}
}
