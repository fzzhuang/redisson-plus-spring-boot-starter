package cn.fzzhuang.starter.redisson.annotation;

import cn.fzzhuang.starter.redisson.model.DataType;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * 多级缓存更新注解
 *
 * @author Fu.zhizhuang
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MultiCachePut {

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
    String[] keys() default {};

    /**
     * 数据类型
     */
    DataType type() default DataType.DEFAULT;

    /**
     * 缓存超时时间
     */
    long expire() default 0L;

    /**
     * 时间单位
     */
    TimeUnit unit() default TimeUnit.MINUTES;
}
