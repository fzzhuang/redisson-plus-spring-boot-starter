/**
 * 项目名称:  redisson-plus-spring-boot-starter
 * 公司名称:  YiShoTech
 * All rights Reserved, Designed By YiShoTech 2023-2024
 */
package cn.yishotech.starter.lock;

import org.redisson.api.RLock;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * <p>类路径:cn.yishotech.starter.lock.DistributedLock</p>
 * <p>类描述:</p>
 * <p>创建人:jason zong</p>
 * <p>创建时间:2024/10/05 01:27</p>
 */
public interface IDistributedLock {

    /**
     * 加锁
     *
     * @param lockKey key
     * @return 锁
     */
    RLock lock(String lockKey);

    /**
     * 加锁
     *
     * @param lockKey   key
     * @param leaseTime 超时时间，默认单位秒
     * @return 锁
     */
    RLock lock(String lockKey, long leaseTime);

    /**
     * 加锁
     *
     * @param lockKey   key
     * @param leaseTime 超时时间
     * @param timeUnit  单位
     * @return 锁
     */
    RLock lock(String lockKey, long leaseTime, TimeUnit timeUnit);


    /**
     * 尝试获取锁
     *
     * @param lockKey   key
     * @param waitTime  最大等待时间
     * @param leaseTime 超过指定的时间后释放锁
     * @param timeUnit  单位
     * @return 锁是否获取成功
     */
    boolean tryLock(String lockKey, long waitTime, long leaseTime, TimeUnit timeUnit);

    /**
     * 尝试获取锁
     *
     * @param lockKey   lock key
     * @param leaseTime 超时时间
     * @param timeUnit  单位
     * @return 锁是否获取成功
     */
    boolean tryLock(String lockKey, long leaseTime, TimeUnit timeUnit);


    /**
     * 解锁
     *
     * @param lockKey key
     */
    void unlock(String lockKey);

    /**
     * 解锁
     *
     * @param lock 锁
     */
    void unlock(RLock lock);

    /**
     * 释放锁
     *
     * @param lockKey key
     */
    void release(String lockKey);

    /**
     * 执行分布式锁
     *
     * @param lockKey   lock key
     * @param waitTime  等待时间
     * @param leaseTime 释放时间
     * @param timeUnit  单位
     * @param supplier  执行方法
     * @param <T>       泛型
     */
    <T> void execute(String lockKey, long waitTime, long leaseTime, TimeUnit timeUnit, Supplier<T> supplier);

    /**
     * 执行分布式锁
     *
     * @param lockKey  lock key
     * @param timeout  超时时间
     * @param supplier 执行方法
     * @param <T>      泛型
     */
    <T> void execute(String lockKey, long timeout, Supplier<T> supplier);

    /**
     * 执行分布式锁
     *
     * @param lockKey  lock key
     * @param supplier 执行方法
     * @param <T>      泛型
     */
    <T> void execute(String lockKey, Supplier<T> supplier);
}
