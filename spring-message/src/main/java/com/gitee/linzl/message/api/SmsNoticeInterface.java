package com.gitee.linzl.message.api;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping(value = "/sms")
public interface SmsNoticeInterface {
	@PostMapping(value = "/send")
	public void sendSmsnMsg(@RequestParam("mobile") String mobile, @RequestParam("content") String content,
			@RequestParam("templateId") String templateId);
}
