package cn.fzzhuang.starter.redisson.lock.strategy;

import cn.fzzhuang.starter.redisson.model.LockInfo;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

/**
 * 公平锁实现
 *
 * @author Fu.zhizhuang
 */
public class FairLock extends AbstractLock {
    public FairLock(RedissonClient redissonClient, LockInfo lockInfo) {
        this.redissonClient = redissonClient;
        this.lockInfo = lockInfo;
    }

    @Override
    protected RLock getLock(String name) {
        return redissonClient.getFairLock(name);
    }
}
