package com.gitee.linzl.model;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

/**
 * @description 必须序列化，才可以通过消息队列传递
 * @author linzl
 * @email 2225010489@qq.com
 * @date 2018年2月10日
 */
@Setter
@Getter
public class User implements Serializable {
	private static final long serialVersionUID = 1L;

	private String id;
	private String name;
	private String pass;

	private long times;
	private String msg;
}