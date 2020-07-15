package com.gitee.linzl.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gitee.linzl.redis.lock.RedissonDistributedLocker;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/demo")
@Slf4j
public class DemoController {
	@Autowired
	private RedisTemplate<String, String> redisTemplate;

	@Autowired
	private RedissonDistributedLocker distributedLocker;

	@GetMapping("/lock")
	public String lock(@RequestParam("sid") String serverId) {
		Long counter = 0L;
		// lock方法，会在Redis中设置一个TEST的key,用于锁住程序
		distributedLocker.lock("TEST");
		try {
			// 没有加锁，就会出现打印出来的counter不是0
			counter = redisTemplate.opsForValue().increment("COUNTER", 1);
			counter = redisTemplate.opsForValue().increment("COUNTER", -1);
			log.info("Request Thread - " + counter + "[" + serverId + "] locked and begun...");
		} catch (Exception ex) {
			log.error("Error occurred");
		} finally {
			distributedLocker.unLock("TEST");
			log.info("Request Thread - " + counter + "[" + serverId + "] unlocked...");
		}
		return "lock-" + counter + "[" + serverId + "]";
	}

}