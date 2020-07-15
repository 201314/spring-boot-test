package com.gitee.linzl.redis.lock;

import java.util.concurrent.TimeUnit;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RedissonDistributedLocker {
	@Autowired
	private RedissonClient redissonClient;

	public void lock(String lockKey) {
		RLock lock = redissonClient.getLock(lockKey);
		lock.lock();
	}

	public void unLock(String lockKey) {
		RLock lock = redissonClient.getLock(lockKey);
		lock.unlock();
	}

	/**
	 * 公平锁，保证执行顺序
	 * 
	 * @param lockKey
	 */
	public void fairLock(String lockKey) {
		RLock lock = redissonClient.getFairLock(lockKey);
		lock.lock();
	}

	public void unFairLock(String lockKey) {
		RLock lock = redissonClient.getFairLock(lockKey);
		lock.unlock();
	}

	public void lock(String lockKey, int leaseTime) {
		RLock lock = redissonClient.getLock(lockKey);
		lock.lock(leaseTime, TimeUnit.SECONDS);
	}

	public void lock(String lockKey, TimeUnit unit, int timeout) {
		RLock lock = redissonClient.getLock(lockKey);
		lock.lock(timeout, unit);
	}
}