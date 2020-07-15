package com.gitee.linzl.model;

import java.io.Serializable;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class MessageLog implements Serializable {
	private static final long serialVersionUID = 1L;
	/**
	 * 消息ID
	 */
	private String messageId;
	/**
	 * 消息体
	 */
	private String message;
	/**
	 * 重试次数
	 */
	private Integer tryCount = 0;
	/**
	 * 下次重试时间
	 */
	private Date nextTryTime;
	/**
	 * 0 处理中，11发送成功，12发送失败，21消费回复消费成功，22回复消费失败
	 */
	private Integer status;
	/**
	 * 创建时间
	 */
	private Date creatTime;
	/**
	 * 修改时间
	 */
	private Date updateTime;
}
