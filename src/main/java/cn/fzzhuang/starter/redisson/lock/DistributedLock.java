package cn.fzzhuang.starter.redisson.lock;

import org.redisson.api.RLock;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * 分布式锁接口
 *
 * @author Fu.zhizhuang
 */
public interface DistributedLock {

    /**
     * 加锁
     *
     * @param lockKey keys
     * @return 锁
     */
    RLock lock(String lockKey);

    /**
     * 加锁
     *
     * @param lockKey   keys
     * @param leaseTime 超时时间，默认单位秒
     * @return 锁
     */
    RLock lock(String lockKey, long leaseTime);

    /**
     * 加锁
     *
     * @param lockKey   keys
     * @param leaseTime 超时时间
     * @param timeUnit  单位
     * @return 锁
     */
    RLock lock(String lockKey, long leaseTime, TimeUnit timeUnit);


    /**
     * 尝试获取锁
     *
     * @param lockKey   keys
     * @param waitTime  最大等待时间
     * @param leaseTime 超过指定的时间后释放锁
     * @param timeUnit  单位
     * @return 锁是否获取成功
     */
    boolean tryLock(String lockKey, long waitTime, long leaseTime, TimeUnit timeUnit);

    /**
     * 尝试获取锁
     *
     * @param lockKey   lock keys
     * @param leaseTime 超时时间
     * @param timeUnit  单位
     * @return 锁是否获取成功
     */
    boolean tryLock(String lockKey, long leaseTime, TimeUnit timeUnit);


    /**
     * 解锁
     *
     * @param lockKey keys
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
     * @param lockKey keys
     */
    void release(String lockKey);

    /**
     * 执行分布式锁
     *
     * @param lockKey   lock keys
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
     * @param lockKey  lock keys
     * @param timeout  超时时间
     * @param supplier 执行方法
     * @param <T>      泛型
     */
    <T> void execute(String lockKey, long timeout, Supplier<T> supplier);

    /**
     * 执行分布式锁
     *
     * @param lockKey  lock keys
     * @param supplier 执行方法
     * @param <T>      泛型
     */
    <T> void execute(String lockKey, Supplier<T> supplier);
}
