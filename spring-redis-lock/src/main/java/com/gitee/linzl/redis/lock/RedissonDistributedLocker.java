package com.gitee.linzl.redis.lock;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Component
public class RedissonDistributedLocker {
    @Autowired
    private RedissonClient redissonClient;

    public <R> R lock(String lockKey, long timeOutSecond, Supplier<R> supplier) {
        RLock lock = redissonClient.getLock(lockKey);
        try {
            lock.lock(timeOutSecond, TimeUnit.SECONDS);
            return supplier.get();
        } finally {
            lock.unlock();
        }
    }

    public <R> R tryLock(String lockKey, long timeOutSecond, Supplier<R> supplier) {
        RLock lock = redissonClient.getLock(lockKey);
        try {
            lock.tryLock(1, timeOutSecond, TimeUnit.SECONDS);
            return supplier.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
            return null;
        }
    }

    /**
     * 公平锁，保证执行顺序
     *
     * @param lockKey
     */
    public <R> R fairLock(String lockKey, long timeOutSecond, Supplier<R> supplier) {
        RLock lock = redissonClient.getFairLock(lockKey);
        try {
            lock.lock(timeOutSecond, TimeUnit.SECONDS);
            return supplier.get();
        } finally {
            lock.unlock();
        }
    }

    public <R> R tryFairLock(String lockKey, long timeOutSecond, Supplier<R> supplier) {
        RLock lock = redissonClient.getFairLock(lockKey);
        try {
            lock.tryLock(1, timeOutSecond, TimeUnit.SECONDS);
            return supplier.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
            return null;
        }
    }
}