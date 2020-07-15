package com.gitee.linzl.mq;

import java.io.IOException;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.rabbitmq.client.Channel;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ErrorRetryReceiver {
	@RabbitListener(bindings = {
			@QueueBinding(value = @Queue(name = "retryQueue", durable = "true", autoDelete = "false"),

					exchange = @Exchange(name = "retryExchange"),

					key = { "retryKey" }) })
	@RabbitHandler
	public void tryReceive(@Payload String msg, Message message, Channel channel) {
		log.debug("测试重试消息");
		try {
			int i = 1 / 0;
		} finally {
			try {// 一定要做确认，否则消息会一直存在在队列中
					// 必须使用channel进行消息确认，包括消费成功或消费失败
				channel.basicReject(message.getMessageProperties().getDeliveryTag(), false);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	// 将处理异常的信息进入该队列进行登记,比如入库数据库
	// 在配置RepublishMessageRecoverer,参考ConsumerApplication
	@RabbitListener(bindings = {
			@QueueBinding(value = @Queue(name = "queueerror", durable = "true", autoDelete = "false"),

					exchange = @Exchange(name = "exchangemsxferror"),

					key = { "routingkeymsxferror" }) })
	@RabbitHandler
	public void writeDB(@Payload String msg, Message message, Channel channel) {
		log.debug("处理错误信息:" + msg);
		try {
			channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}