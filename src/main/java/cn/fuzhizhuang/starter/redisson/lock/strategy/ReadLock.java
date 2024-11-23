package cn.fuzhizhuang.starter.redisson.lock.strategy;

import cn.fuzhizhuang.starter.redisson.model.LockInfo;
import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RedissonClient;

/**
 * 读锁实现
 *
 * @author Fu.zhizhuang
 */
public class ReadLock extends AbstractLock {

    public ReadLock(RedissonClient redissonClient, LockInfo lockInfo) {
        this.redissonClient = redissonClient;
        this.lockInfo = lockInfo;
    }

    @Override
    protected RLock getLock(String name) {
        RReadWriteLock readWriteLock = redissonClient.getReadWriteLock(name);
        return readWriteLock.readLock();
    }
}
