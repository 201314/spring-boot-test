package com.gitee.linzl.message.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * @description:邮件服务
 * @author: yanyanhui
 */
@Service
public class SendMailService {
	@Autowired
	private JavaMailSender mailSender;
	@Autowired
	private SimpleMailMessage simpleMailMessage;

	/**
	 * 发送邮件
	 * 
	 * @param to
	 *            邮件收件人地址
	 * @param title
	 *            邮件标题
	 * @param text
	 *            内容
	 */
	public void sendMsg(String to, String title, String text) {
		SimpleMailMessage msg = new SimpleMailMessage(simpleMailMessage);
		msg.setTo(to);
		msg.setSubject(title);
		msg.setText(text);
		mailSender.send(msg);
	}

	/**
	 * 发送邮件
	 * 
	 * @param to
	 *            邮件收件人地址
	 * @param copyEmail
	 *            邮件抄送地址
	 * @param title
	 *            邮件标题
	 * @param text
	 *            内容
	 */
	public void sendMsg(String to, String[] copyEmail, String title, String text) {
		SimpleMailMessage msg = new SimpleMailMessage(simpleMailMessage);
		msg.setTo(to);
		msg.setCc(copyEmail);
		msg.setSubject(title);
		msg.setText(text);
		mailSender.send(msg);
	}
}
