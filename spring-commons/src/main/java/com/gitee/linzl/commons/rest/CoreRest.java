package com.gitee.linzl.commons.rest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用于检测项目健康度、同步时间等
 * 
 * @description
 * @author linzl
 * @email 2225010489@qq.com
 * @date 2019年2月27日
 */
@RestController
public class CoreRest {
	/**
	 * 检测服务是否还活着
	 * 
	 * @return
	 */
	@RequestMapping(value = {"","ping"})
	public String ping() {
		return "pong";
	}

	/**
	 * web应用与服务器进行时间同步
	 * 
	 * @return
	 */
	@RequestMapping("sync_time")
	public Long syncTime() {
		return System.currentTimeMillis();
	}
}
