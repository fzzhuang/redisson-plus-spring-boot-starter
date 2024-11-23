package cn.fuzhizhuang.starter.redisson.annotation;

import cn.fuzhizhuang.starter.redisson.model.DataType;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * Redisson缓存存储注解
 *
 * @author Fu.zhizhuang
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RedissonCachePut {

    /**
     * 缓存key前缀
     */
    String prefix() default "";

    /**
     * 缓存键
     */
    String[] keys() default "";

    /**
     * 缓存数据类型
     */
    DataType type() default DataType.DEFAULT;

    /**
     * 缓存超时时间
     */
    long expire() default 0;

    /**
     * 时间单位
     */
    TimeUnit timeUnit() default TimeUnit.MINUTES;

}
