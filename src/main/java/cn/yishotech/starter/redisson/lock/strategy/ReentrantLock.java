/**
 * 项目名称:  redisson-plus-spring-boot-starter
 * 公司名称:  YiShoTech
 * All rights Reserved, Designed By YiShoTech 2023-2024
 */
package cn.yishotech.starter.redisson.lock.strategy;

import cn.yishotech.starter.redisson.model.LockInfo;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

/**
 * <p>类路径:cn.yishotech.starter.lock.strategy.ReentrantLock</p>
 * <p>类描述:可重入锁实现</p>
 * <p>创建人:jason zong</p>
 * <p>创建时间:2024/10/04 23:48</p>
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
