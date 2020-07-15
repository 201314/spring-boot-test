package com.baomidou.springboot.services;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MyService implements IMyService {
	public void add() {
		log.debug("========MyService add========");
	}

	public void doSomeThing(String someThing) {
		log.debug("执行被拦截的方法：" + someThing);
	}
}
