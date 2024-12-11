package cn.fzzhuang.starter.redisson.annotation;

import cn.fzzhuang.starter.redisson.model.LockType;

import java.lang.annotation.*;

/**
 * 分布式锁注解
 *
 * @author Fu.zhizhuang
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DistributedLock {

    /**
     * 锁名称
     *
     * @return 锁名称
     */
    String name() default "";

    /**
     * 锁类型，默认可重入锁
     *
     * @return 锁类型
     */
    LockType type() default LockType.Reentrant;

    /**
     * 尝试加锁，最多等待时间，默认10秒
     *
     * @return 最多等待时间
     */
    long waitTime() default 10L;

    /**
     * 上锁以后自动解锁时间，默认300秒
     *
     * @return 自动解锁时间
     */
    long leaseTime() default 300L;

    /**
     * 自定义业务key
     *
     * @return 业务key
     */
    String[] keys() default {};
}
