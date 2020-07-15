package com.gitee.linzl.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gitee.linzl.advanced.AdvancedProducer;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/kafka")
@Slf4j
public class KafkaController {

	@Autowired
	private AdvancedProducer advancedProducer;

	@GetMapping("")
	public void manyToMany() {
		advancedProducer.send("advancedtopic", "主题内容", 10);
	}

	@GetMapping("/logback")
	public void logback() {
//		log.debug("我是loback输出的消息，快消费掉吧");
		log.error("English我就是安装着玩的DevOps====");
		log.error("English哈哈，连任连任====");
	}

}