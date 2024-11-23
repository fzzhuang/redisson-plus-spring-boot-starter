package cn.fuzhizhuang.starter.redisson.annotation;

import cn.fuzhizhuang.starter.redisson.model.DataType;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * 多级缓存注解
 *
 * @author Fu.zhizhuang
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MultiCache {

    /**
     * 一级缓存名称
     */
    String cacheName() default "";

    /**
     * 缓存前缀
     */
    String prefix() default "";

    /**
     * 缓存key
     */
    String[] keys() default "";

    /**
     * 缓存过期时间，单位分钟
     */
    int expire() default 0;

    /**
     * 缓存过期时间单位
     */
    TimeUnit unit() default TimeUnit.MINUTES;

    /**
     * 数据类型
     */
    DataType type() default DataType.DEFAULT;
}
