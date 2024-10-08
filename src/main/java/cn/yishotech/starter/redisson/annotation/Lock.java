/**
 * 项目名称:  redisson-plus-spring-boot-starter
 * 公司名称:  YiShoTech
 * All rights Reserved, Designed By YiShoTech 2023-2024
 */
package cn.yishotech.starter.redisson.annotation;

import cn.yishotech.starter.redisson.model.LockType;

import java.lang.annotation.*;

/**
 * <p>类路径:cn.yishotech.starter.annotation.Lock</p>
 * <p>类描述:分布式锁注解</p>
 * <p>创建人:jason zong</p>
 * <p>创建时间:2024/10/05 00:02</p>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Lock {

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
    long waitTime() default 10;

    /**
     * 上锁以后自动解锁时间，默认300秒
     *
     * @return 自动解锁时间
     */
    long leaseTime() default 5 * 60;

    /**
     * 自定义业务key
     *
     * @return 业务key
     */
    String[] keys() default {};
}
