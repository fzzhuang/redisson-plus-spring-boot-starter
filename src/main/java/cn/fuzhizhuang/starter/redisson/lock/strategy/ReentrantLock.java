package cn.fuzhizhuang.starter.redisson.lock.strategy;

import cn.fuzhizhuang.starter.redisson.model.LockInfo;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

/**
 * 可重入锁实现
 *
 * @author Fu.zhizhuang
 */
public class ReentrantLock extends AbstractLock {

    public ReentrantLock(RedissonClient redissonClient, LockInfo lockInfo) {
        this.redissonClient = redissonClient;
        this.lockInfo = lockInfo;
    }

    @Override
    protected RLock getLock(String name) {
        return redissonClient.getLock(name);
    }
}
