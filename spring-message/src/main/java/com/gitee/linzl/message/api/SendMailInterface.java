package com.gitee.linzl.message.api;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.gitee.linzl.message.api.request.MailRequest;

@RequestMapping(value = "/mail")
public interface SendMailInterface {
	@PostMapping(value = "/send")
	public void sendMail(@RequestBody MailRequest req);
}
