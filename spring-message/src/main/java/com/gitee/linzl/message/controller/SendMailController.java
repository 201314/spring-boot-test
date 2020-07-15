package com.gitee.linzl.message.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.gitee.linzl.message.api.SendMailInterface;
import com.gitee.linzl.message.api.request.MailRequest;
import com.gitee.linzl.message.service.SendMailService;

@RestController
public class SendMailController implements SendMailInterface {
	@Autowired
	private SendMailService sendMailService;

	@Override
	public void sendMail(@RequestBody MailRequest req) {
		String copyEmail = req.getCopyEmail();

		if (null == copyEmail || copyEmail.isEmpty()) {// 普通邮件
			sendMailService.sendMsg(req.getReceiverMail(), req.getSubject(), req.getMailContents());

		} else {// 增加抄送人
			sendMailService.sendMsg(req.getReceiverMail(), copyEmail.split(";"), req.getSubject(),
					req.getMailContents());
		}
	}

}
