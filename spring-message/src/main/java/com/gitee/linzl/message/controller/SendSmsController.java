package com.gitee.linzl.message.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gitee.linzl.message.api.SmsNoticeInterface;
import com.gitee.linzl.message.service.SendSmsService;

@RestController
public class SendSmsController implements SmsNoticeInterface {
	@Autowired
	private SendSmsService sendSmsService;

	@Override
	public void sendSmsnMsg(@RequestParam("mobile") String mobile, @RequestParam("content") String content,
			@RequestParam("templateId") String templateId) {
	}
}
