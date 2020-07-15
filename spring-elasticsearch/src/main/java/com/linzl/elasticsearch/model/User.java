package com.linzl.elasticsearch.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class User {

	/**
	 * 毕业院校
	 */
	private String edu;

	/**
	 * 年龄
	 */
	private Integer age;

	/**
	 * 工作
	 */
	private String work;

	/**
	 * 名字
	 */
	private String name;

}
