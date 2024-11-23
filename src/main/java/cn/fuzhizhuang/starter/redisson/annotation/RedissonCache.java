package cn.fuzhizhuang.starter.redisson.annotation;

import cn.fuzhizhuang.starter.redisson.model.DataType;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * Redisson缓存注解
 *
 * @author Fu.zhizhuang
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RedissonCache {

    /**
     * 缓存key前缀
     */
    String prefix() default "";

    /**
     * 缓存键
     */
    String[] keys() default "";

    /**
     * 缓存过期时间，单位为分钟，默认为0，表示不过期
     */
    long expire() default 0;

    /**
     * 缓存过期时间单位，默认为分钟
     */
    TimeUnit unit() default TimeUnit.MINUTES;

    /**
     * 查询数据类型
     */
    DataType type() default DataType.DEFAULT;
}
