package cn.fuzhizhuang.starter.redisson.lock.strategy;

import cn.fuzhizhuang.starter.redisson.lock.ILock;
import cn.fuzhizhuang.starter.redisson.model.LockInfo;
import lombok.Data;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import java.util.concurrent.TimeUnit;

/**
 * 抽象锁
 *
 * @author Fu.zhizhuang
 */
@Data
public abstract class AbstractLock implements ILock {

    protected LockInfo lockInfo;

    protected RedissonClient redissonClient;

    protected RLock lock;

    @Override
    public boolean acquire() {
        try {
            // 获取锁
            lock = getLock(lockInfo.getName());
            // 尝试加锁
            return lock.tryLock(lockInfo.getWaitTime(), lockInfo.getLeaseTime(), TimeUnit.SECONDS);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void release() {
        // 查询当前线程是否持有此锁
        if (lock.isHeldByCurrentThread()) {
            // 释放锁
            lock.unlockAsync();
        }
    }

    /**
     * 获取锁
     *
     * @param name 锁名称
     * @return 锁
     */
    protected abstract RLock getLock(String name);
}
