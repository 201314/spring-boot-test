package com.gitee.linzl.rest;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gitee.linzl.model.User;
import com.gitee.linzl.mq.DLXSender;
import com.gitee.linzl.mq.DirectSender;
import com.gitee.linzl.mq.ErrorRetrySender;
import com.gitee.linzl.mq.FanoutSender;
import com.gitee.linzl.mq.TopicSender;

@RestController
@RequestMapping
public class MQRest {
	@Autowired
	private DirectSender sender;

	@GetMapping("direct")
	public String direct() {
		for (int i = 0; i < 10; i++) {
			User user = new User();
			user.setId(String.valueOf(i));
			user.setName("name" + i);
			sender.send(user);
			try {
				TimeUnit.SECONDS.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return "";
	}

	@Autowired
	private TopicSender topicSender;

	@GetMapping("topic")
	public String topic() {
		for (int i = 0; i < 10; i++) {
			topicSender.send("登录送积分" + i);
		}
		return "";
	}

	@Autowired
	private FanoutSender fanoutSender;

	@GetMapping("fanout")
	public String fanout() {
		for (int i = 0; i < 10; i++) {
			fanoutSender.send("站内广播" + i);
		}
		return "";
	}

	@Autowired
	private DLXSender dlxSender;

	@GetMapping("dlx")
	public String dlx() {
		for (int i = 0; i < 10; i++) {
			dlxSender.send("死信消息" + i);
		}
		return "";
	}

	@Autowired
	private ErrorRetrySender errorRetrySender;

	@GetMapping("retry")
	public String retry() {
		errorRetrySender.send("测试retry3次消费" + LocalDateTime.now());
		return "";
	}

}
