package com.gitee.linzl.mq.producer;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;

/**
 * 原生写法：生产者
 */
public class ProducerOriginal {

    public static void main(String args[]) throws Exception {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setPassword("guest");
        connectionFactory.setPassword("guest");
        connectionFactory.setHost("192.168.1.141");
        connectionFactory.setPort(5672);
        Connection connection = connectionFactory.newConnection();
        Channel channel = connection.createChannel();
        String exchangeName = "test";
        String queueName = "test";
        String routingkey = "routingkey";
        String bindingkey = routingkey;
        //设置交换机,直连，持久化，非自动删除，参数
        channel.exchangeDeclare(exchangeName, BuiltinExchangeType.FANOUT, true, false, null);
        //channel.exchangeDeclare(exchangeName, BuiltinExchangeType.DIRECT,true,false,null);
        /**
         * BuiltinExchangeType.FANOUT  不需要绑定 bindingkey与routingkey exchange直接将消息投递到queue
         * BuiltinExchangeType.DIRECT  需要绑定  且bindingkey与routingkey一致时 exchange投递消息到queue
         * BuiltinExchangeType.TOPIC   需要绑定  模糊匹配,规则如下
         * RoutingKey 为一个点号"."分隔的字符串(被点号"。"分隔开的每段独立的字符,如com.rabbit.demo,com.rabbit.test）
         * BindingKey RoutingKey 样也是点号"."分隔的字符串;
         * BindingKey 中可以存在两种特殊字符串"*"和"#"，用于做模糊匹配，其中"#"用于匹配多个（零个到多个），* 匹配任意一个单词。
         *    如果binding_key 是 “#” - 它会接收所有的Message，不管routing_key是什么，就像是fanout exchange。
         *    如果 “*” and “#” 没有被使用，那么topic exchange就变成了direct exchange。
         * BuiltinExchangeType.HEADERS 不需要绑定
         *   消费者arguments指定“x-match”，这个键的Value可以是any或者all，这代表消息携带的Hash是需要全部匹配(all)，还是仅匹配一个键(any)就可以了
         */
        /**
         * 持久化:数据会保存到磁盘，重启rabbitmq数据依旧存在
         *
         * 排他:该队列仅对首次声明它的连接可见，并在连接断开时自动删除。
         * 需要注意三点:排他队列是基于连接( Connection) 可见的，同一个连接的不同信道 (Channel)
         * 是可以同时访问同一连接创建的排他队列; "首次"是指如果一个连接己经声明了
         * 排他队列，其他连接是不允许建立同名的排他队列的，这个与普通队列不同:即使该队
         * 列是持久化的，一旦连接关闭或者客户端退出，该排他队列都会被自动删除，这种队列
         * 适用于一个客户端同时发送和读取消息的应用场景。
         *
         *
         * 自动删除:设置是否自动删除。为 true 则设置队列为自动删除。自动删除的前提是:
         * 至少有一个消费者连接到这个队列，之后所有与这个队列连接的消费者都断开时，才会
         * 自动删除。不能把这个参数错误地理解为: "当连接到此队列的所有客户端断开时，这
         * 个队列自动删除"，因为生产者客户端创建这个队列，或者没有消费者客户端与这个队
         * 列连接时，都不会自动删除这个队列。
         *
         * 根据业务数据，最好提前建好exchange，queue,及绑定关系，生产端，消费端可以避免很多错误,如exchang创建失败，
         * 绑定关系不确定导致消息投递失败
         *
         * rabbitmq  clinet 属性集
         * props 消息的基本属性集，其包含 14 个属性成员，分别有 contentType
         * content ncoding headers Map<String Object>) deliveryMode priority
         * correlationld replyTo expiration messageld timestamp type userld
         * appld cluster 。
         *
         *   mandatory 参数设为 true 时，交换器无法根据自身的类型和路由键的队列，
         *   那么 RabbitM 会调用 Basic.Return 命令将消息返回给生产者 数设置为 false 时，出现上述情形，
         *   则消息直接被丢弃 那么生产者如何获取到没有被正确路由到合适队列的消息呢?这时channel addReturnListener 来添加 ReturnListener 监昕器实现。 
         *   通过rabbitmq备份队列，当消息发送不到主队列，将自动发送到备份队列，设置了备份队列，mandatory参数将无效
         */

        //设置队列，持久化，非排他，非自动删除
        channel.queueDeclare(queueName, true, false, false, null);
        //channel.queueBind(queueName,exchangeName, bindingkey);
        for (int i = 0; i < 10; i++) {
            channel.basicPublish(exchangeName, routingkey,
                    MessageProperties.PERSISTENT_TEXT_PLAIN, "hi,Rabbitmq".getBytes());
        }
        channel.close();
        connection.close();
    }
}