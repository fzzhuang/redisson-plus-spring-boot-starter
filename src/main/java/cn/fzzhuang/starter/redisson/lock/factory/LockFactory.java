package cn.fzzhuang.starter.redisson.lock.factory;

import cn.fzzhuang.starter.redisson.lock.Lock;
import cn.fzzhuang.starter.redisson.lock.strategy.FairLock;
import cn.fzzhuang.starter.redisson.lock.strategy.ReadLock;
import cn.fzzhuang.starter.redisson.lock.strategy.ReentrantLock;
import cn.fzzhuang.starter.redisson.lock.strategy.WriteLock;
import cn.fzzhuang.starter.redisson.model.LockInfo;
import jakarta.annotation.Resource;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

/**
 * 分布式锁工厂
 *
 * @author Fu.zhizhuang
 */
@Component
public class LockFactory {

    @Resource
    private RedissonClient redissonClient;

    public Lock getLock(LockInfo lockInfo) {
        return switch (lockInfo.getLockType()) {
            case Fair -> new FairLock(redissonClient, lockInfo);
            case Read -> new ReadLock(redissonClient, lockInfo);
            case Write -> new WriteLock(redissonClient, lockInfo);
            default -> new ReentrantLock(redissonClient, lockInfo);
        };
    }
}
