/**
 * 项目名称:  redisson-plus-spring-boot-starter
 * 公司名称:  YiShoTech
 * All rights Reserved, Designed By YiShoTech 2023-2024
 */
package cn.yishotech.starter.lock.strategy;

import cn.yishotech.starter.model.LockInfo;
import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RedissonClient;

/**
 * <p>类路径:cn.yishotech.starter.lock.strategy.ReadLock</p>
 * <p>类描述:读锁实现</p>
 * <p>创建人:jason zong</p>
 * <p>创建时间:2024/10/04 23:50</p>
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
