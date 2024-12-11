package cn.fzzhuang.starter.redisson.lock.strategy;

import cn.fzzhuang.starter.redisson.model.LockInfo;
import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RedissonClient;

/**
 * 写锁实现
 *
 * @author Fu.zhizhuang
 */
public class WriteLock extends AbstractLock {

    public WriteLock(RedissonClient redissonClient, LockInfo lockInfo) {
        this.redissonClient = redissonClient;
        this.lockInfo = lockInfo;
    }

    @Override
    protected RLock getLock(String name) {
        RReadWriteLock readWriteLock = redissonClient.getReadWriteLock(name);
        return readWriteLock.writeLock();
    }
}
