/**
 * 项目名称:  redisson-plus-spring-boot-starter
 * 公司名称:  YiShoTech
 * All rights Reserved, Designed By YiShoTech 2023-2024
 */
package cn.yishotech.starter.lock.factory;

import cn.yishotech.starter.lock.ILock;
import cn.yishotech.starter.lock.strategy.FairLock;
import cn.yishotech.starter.lock.strategy.ReadLock;
import cn.yishotech.starter.lock.strategy.ReentrantLock;
import cn.yishotech.starter.lock.strategy.WriteLock;
import cn.yishotech.starter.model.LockInfo;
import jakarta.annotation.Resource;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

/**
 * <p>类路径:cn.yishotech.starter.lock.factory.LockFactory</p>
 * <p>类描述:分布式锁工厂</p>
 * <p>创建人:jason zong</p>
 * <p>创建时间:2024/10/04 23:53</p>
 */
@Component
public class LockFactory {

    @Resource
    private RedissonClient redissonClient;

    public ILock getLock(LockInfo lockInfo) {
        return switch (lockInfo.getLockType()) {
            case Fair -> new FairLock(redissonClient, lockInfo);
            case Read -> new ReadLock(redissonClient, lockInfo);
            case Write -> new WriteLock(redissonClient, lockInfo);
            default -> new ReentrantLock(redissonClient, lockInfo);
        };
    }
}
