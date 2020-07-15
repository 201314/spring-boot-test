package com.gitee.linzl.message.api.request;

public class MailRequest {
	private String receiverMail;//收件人邮箱
	private String subject;//邮件主题
	private String mailContents;//邮件内容
	private String copyEmail;//抄送人，多个以；隔开
	public String getReceiverMail() {
		return receiverMail;
	}
	public void setReceiverMail(String receiverMail) {
		this.receiverMail = receiverMail;
	}
	public String getMailContents() {
		return mailContents;
	}
	public void setMailContents(String mailContents) {
		this.mailContents = mailContents;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getCopyEmail() {
		return copyEmail;
	}
	public void setCopyEmail(String copyEmail) {
		this.copyEmail = copyEmail;
	}
	
	
}
