package com.gitee.linzl.mq.consumer;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.alibaba.fastjson.JSON;
import com.gitee.linzl.constants.MQConstant;
import com.gitee.linzl.model.MessageLog;
import com.gitee.linzl.model.User;
import com.rabbitmq.client.Channel;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class DirectReceiver {
	@Autowired
	private TransactionTemplate transaction;

	/**
	 * 接收到的消息体
	 * 
	 * [payload=com.rabbit.model.User@20947bf6,
	 * 
	 * headers={amqp_receivedDeliveryMode=PERSISTENT,
	 * amqp_receivedRoutingKey=replyTo.route.key,
	 * amqp_receivedExchange=reply_to.exchange, amqp_deliveryTag=1,
	 * amqp_replyTo=sms.send.queue, amqp_consumerQueue=reply_to.queue,
	 * amqp_redelivered=false, id=5206b1a6-08af-71ca-e8fc-62173f72716f,
	 * amqp_consumerTag=amq.ctag-jSNNPLdFd-2een-qJdJgpw,
	 * contentType=application/x-java-serialized-object, timestamp=1519723803696}
	 * 
	 * ]
	 * 
	 * @param user
	 * @param message
	 * @param correlationId
	 * @throws IOException
	 * 
	 *                     支付宝完成扣钱的动作时,记录消息数据,将消息数据和业务数据存在同一个数据库实例中.
	 * 
	 *                     Begin Transaction
	 * 
	 *                     update A set amount=amount-1000 where uid=100;
	 * 
	 *                     insert into message(uid,amount,status) values (1,1000,1)
	 *                     End
	 * 
	 *                     Transaction
	 * 
	 *                     Commit;
	 *                     将支付宝完成扣钱的消息及时发送给余额宝，余额宝完成处理后(也需要记录消息数据,成功发送)返回成功消息，支付宝收到消息后，消除消息表中对应的消息记录，即完成本次扣钱操作.
	 */
	@RabbitListener(bindings = {
			@QueueBinding(value = @Queue(name = MQConstant.SMS_QUEUE, durable = "true", autoDelete = "false"),

					exchange = @Exchange(name = MQConstant.SMS_EXCHANGE, type = ExchangeTypes.DIRECT),

					key = { MQConstant.SMS_ROUTE_KEY }) })
	@RabbitHandler
	public void process(@Payload User user, @Headers Map<String, String> head, Message message, Channel channel) {
		/**
		 * 消费端，消费消息要注意幂等性,多次消费同一消息,结果要同消费成功时的结果一致
		 * 
		 * 这点要靠消费端来完成，尽管消费端可以通过ACK来通知消息队列消息已经被消费，但如果消费端消费了消息，此时ACK过程中的通知出现异常， //
		 * 消息队列会认为消息未被消费会继续发给消费端。
		 */
		if (log.isDebugEnabled()) {
			log.debug("消息ID:【{}】,消息内容:【{}】,DirectReceiver deliveryTag:【{}】",
					message.getMessageProperties().getCorrelationId(), JSON.toJSON(user),
					message.getMessageProperties().getDeliveryTag());
			log.debug("消息Headers内容:【{}】", head);
		}

		// 消息到达时，先通过消息的唯一业务ID去判断业务是否已经处理成功
		// 1.先从MessageLog查询是否成功或失败,有则直接回复上次处理结果
		// MessageLog msgLog =
		// selectMessageLog(message.getMessageProperties().getCorrelationId());
		// if (msgLog!=null) {
		// senderReply.process(msgLog, message, channel);
		// }

		// 2.没有消息记录，则往下走
		MessageLog msgLog = new MessageLog();
		msgLog.setMessageId(message.getMessageProperties().getCorrelationId());

		Boolean msgFlag = transaction.execute(new TransactionCallback<Boolean>() {
			@Override
			public Boolean doInTransaction(TransactionStatus status) {
				msgLog.setCreatTime(new Date());
				msgLog.setStatus(0);
				// 消息日志入库
				// dao.insert(msgLog);
				return true;
			}
		});

		if (msgFlag) {
			msgLog.setStatus(21);
			try {// 一定要做确认，否则消息会一直存在在队列中
					// 必须使用channel进行消息确认，包括消费成功或消费失败
				channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
			} catch (IOException e) {
				msgLog.setStatus(22);
			}
		}
		// 消息的标识，false只确认当前一个消息收到，true确认所有consumer获得的消息
		// channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);

		// 消息确认时使用nack，并且requeue参数传false
		// channel.basicNack(message.getMessageProperties().getDeliveryTag(), false,
		// false);

		// ack返回false，并重新回到队列，api里面解释得很清楚
		// channel.basicNack(message.getMessageProperties().getDeliveryTag(), false,
		// true);

		// 拒绝消息
		// channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
	}
}