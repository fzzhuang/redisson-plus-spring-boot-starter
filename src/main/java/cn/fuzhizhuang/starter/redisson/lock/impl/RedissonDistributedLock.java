package cn.fuzhizhuang.starter.redisson.lock.impl;

import cn.fuzhizhuang.starter.redisson.lock.IDistributedLock;
import jakarta.annotation.Resource;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * Redisson实现的分布式锁
 *
 * @author Fu.zhizhuang
 */
@Component
public class RedissonDistributedLock implements IDistributedLock {

    @Resource
    private RedissonClient redissonClient;

    @Override
    public RLock lock(String lockKey) {
        // 拿不到lock就不罢休，线程一直block
        RLock lock = redissonClient.getLock(lockKey);
        lock.lock();
        return lock;
    }

    @Override
    public RLock lock(String lockKey, long leaseTime) {
        RLock lock = redissonClient.getLock(lockKey);
        lock.lock(leaseTime, TimeUnit.SECONDS);
        return lock;
    }

    @Override
    public RLock lock(String lockKey, long leaseTime, TimeUnit timeUnit) {
        RLock lock = redissonClient.getLock(lockKey);
        lock.lock(leaseTime, timeUnit);
        return lock;
    }

    @Override
    public boolean tryLock(String lockKey, long waitTime, long leaseTime, TimeUnit timeUnit) {
        try {
            RLock lock = redissonClient.getLock(lockKey);
            return lock.tryLock(waitTime, leaseTime, timeUnit);
        } catch (InterruptedException e) {
            return false;
        }
    }

    @Override
    public boolean tryLock(String lockKey, long leaseTime, TimeUnit timeUnit) {
        try {
            RLock lock = redissonClient.getLock(lockKey);
            return lock.tryLock(leaseTime, timeUnit);
        } catch (InterruptedException e) {
            return false;
        }
    }

    @Override
    public void unlock(String lockKey) {
        redissonClient.getLock(lockKey).unlock();
    }

    @Override
    public void unlock(RLock lock) {
        lock.unlock();
    }

    @Override
    public void release(String lockKey) {
        RLock lock = redissonClient.getLock(lockKey);
        // 查询当前线程是否保持锁定
        if (lock.isHeldByCurrentThread()) {
            lock.unlockAsync();
        }
    }

    @Override
    public <T> void execute(String lockKey, long waitTime, long leaseTime, TimeUnit timeUnit, Supplier<T> supplier) {
        boolean locked = tryLock(lockKey, waitTime, leaseTime, timeUnit);
        if (!locked) {
            throw new RuntimeException("lock failed");
        }
        try {
            supplier.get();
        } finally {
            unlock(lockKey);
        }
    }

    @Override
    public <T> void execute(String lockKey, long timeout, Supplier<T> supplier) {
        boolean locked = tryLock(lockKey, timeout, TimeUnit.SECONDS);
        if (!locked) {
            throw new RuntimeException("lock failed");
        }
        try {
            supplier.get();
        } finally {
            unlock(lockKey);
        }
    }

    @Override
    public <T> void execute(String lockKey, Supplier<T> supplier) {
        execute(lockKey, -1, supplier);
    }
}
