package com.gitee.linzl.mq.producer;

import com.gitee.linzl.constants.MQConstant;
import com.gitee.linzl.model.User;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 创建普通消息队列 ，点对点模式
 * <p>
 * sender不关心exchange，queue，bindKey的绑定关系，只关注通过哪个exchange，routingKey去发送消息，调用
 * convertAndSend(String exchange, String routingKey, final Object message) 接口。
 * <p>
 * 发送方的exchange等于接收方的exchange，发送方的routingKey等于接收方的bindKey。二者必须做好约定，保持一致。
 * <p>
 * 同时receiver可以通过一个queue和多个bindkey来和exchange做绑定，实现多个消息的订阅
 * <p>
 * 消费方先发版本，在生产环境创建好队列
 *
 * @author linzl
 * @description
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