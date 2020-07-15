package com.gitee.linzl;

import java.util.Date;
import java.util.UUID;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.gitee.linzl.model.User;
import com.gitee.linzl.mq.DLXSender;
import com.gitee.linzl.mq.FanoutSender;
import com.gitee.linzl.mq.TopicSender;
import com.gitee.linzl.service.IUserService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RabbitMqTest {
	/**
	 * 点对点，消费者们会获取生产者的内容
	 * 
	 * 单生产者,就只使用一个sender,单消费者，就只接收一个queue的内容。
	 * 
	 * 在此只举例多对多的情形
	 * 
	 * 通过打印会发现receiver1、receiver2、receiver3 获取生产者
	 * 
	 * 多生产者-多消费者
	 */
	@Autowired
	private IUserService userSender;

	@Test
	public void directTest() {
		System.out.println(userSender);
		for (int i = 0; i < 10; i++) {
			Date data = new Date();
			User user = new User();
			user.setMsg("点对点==》发第" + i + "短信,当前时间:" + data);
			user.setId(UUID.randomUUID().toString());
			user.setName("名字=" + i);
			userSender.save(user);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Autowired
	private TopicSender topicSender;

	/**
	 * topic exchange类型rabbitmq测试
	 */
	@Test
	public void topicTest() {
		for (int i = 0; i < 10; i++) {
			topicSender.send("订阅==》发第" + i + "短信");
		}
	}

	@Autowired
	private FanoutSender fanoutSender;

	/**
	 * fanout exchange类型rabbitmq测试
	 */
	@Test
	public void fanoutTest() {
		for (int i = 0; i < 10; i++) {
			fanoutSender.send("组播==》发第" + i + "短信");
		}
	}

	@Autowired
	private DLXSender dLXSender;

	@Test
	public void dLXTest() {
		dLXSender.send("测试延迟发送消息");
	}

	// @Autowired
	// private ConfirmSender confirmSender;

	// @Test
	// public void confirm() {
	// confirmSender.send("sender1========confirm");
	// }
}
