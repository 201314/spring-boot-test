package com.gitee.linzl.mq.consumer;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import java.io.IOException;

/**
 * 原生写法：消费者
 */
public class ConsumerOriginal {

    public static void main(java.lang.String args[]) throws Exception {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setPassword("guest");
        connectionFactory.setPassword("guest");
        connectionFactory.setHost("192.168.1.141");
        connectionFactory.setPort(5672);
        Connection connection = connectionFactory.newConnection();
        Channel channel = connection.createChannel();
        /*while(true){
            //单个获取消息，当没有消息返回null，pull模式
            GetResponse response = channel.basicGet("test001", true);
            if(response!=null){
                System.out.println(new String(response.getBody()));
            }
        }*/
        //从queue中手动确认获取消息，push模式
        /**
         * 队列名，自动签收，默认消费者
         * queue 队列的名称:
         * autoAck 设置是否自动确认。建议设成 fa se ，即不自动确认:
         *      utoAck 参数置为 false ，对于 RabbitMQ 服务端而 ，队列中的消息分成了两个部分
         * 部分是等待投递给消费者的消息:一部分是己经投递给消费者，但是还没有收到消费者确认
         * 信号的消息。 如果 RabbitMQ 直没有收到消费者的确认信号，并且消费此消息的消费者己经
         * 断开连接，则 RabbitMQ 会安排该消息重新进入队列，等待投递给下 个消费者，当然也有可
         * 能还是原来的那个消费者。
         * RabbitMQ 会为未确认的消息设置过期时间，它判断此消息是否需要重新投递给消费者的
         * 依据是消费该消息的消费者连接是否己经断开，这么设计的原因是 RabbitMQ 允许消费者
         * 消费 条消息的时间可以很久很久。
         * consumerTag: 消费者标签，用来区分多个消费者:
         * noLocal 设置为 true 则表示不能将同一个 Connectio口中生产者发送的消息传送给
         * 这个 Connection 中的消费者:
         * exclusive 设置是否排他
         * arguments 设置消费者的其他参数:
         * callback 设置消费者的回调函数。用来处理 Rabb itM 推送过来的消息，比如
         * DefaultConsumer 使用时需要客户端重写 (overr e) 其中的方法。
         */
        channel.basicConsume("test", false, new DefaultConsumer(channel) {
            public void handleDelivery(String consumerTag, Envelope envelope,
                                       AMQP.BasicProperties properties, byte[] body) throws IOException {
                System.out.println("消费者1:" + new java.lang.String(body));
                channel.basicAck(envelope.getDeliveryTag(), false);
            }
        });
        //创建多个消费者，默认为平均分摊round-robin
        //不支持队列层面的广播消费
        /**
         * 上面代码中显式地设置 autoAck false 然后在接收到消息之后 行显式 ack
         * (channel basicAck )， 对于消费者来说这 设置是非常 可以防止
         * 丢失。
         */
        channel.basicConsume("queue", false, new DefaultConsumer(channel) {
            public void handleDelivery(String consumerTag, Envelope envelope,
                                       AMQP.BasicProperties properties, byte[] body) throws IOException {
                System.out.println("消费者2：" + new java.lang.String(body));
                channel.basicAck(envelope.getDeliveryTag(), false);
            }
        });
        //单个拒绝接受消息
        //channel.basicReject(111, false);
        /**
         * 多个拒绝消息
         */
        // channel.basicNack();
        /**
         * channel.basicReject 或者 channel.basicNack 中的 requeue 设直为 false ，可
         * 以启用"死信队列"的功能。死信队列可以通过检测被拒绝或者未送达的消息来追踪问题
         */
        /***
         * Basic Consume 将信道 (Channel) 直为接收模式，直到取消队列的订阅为止。在接收
         * 模式期间， RabbitMQ 会不断地推送消息给消费者，当然推送消息的个数还是会受到 Basic.Qos
         * 的限制.如果只想从队列获得单条消息而不是持续订阅，建议还是使用 Basic.Get 进行消费.但
         * 是不能将 Basic.Get 放在一个循环里来代替 Basic.Consume ，这样做会严重影响 RabbitMQ
         * 的性能.如果要实现高吞吐量，消费者理应使用 Basic.Consume 方法。
         */
    }
}