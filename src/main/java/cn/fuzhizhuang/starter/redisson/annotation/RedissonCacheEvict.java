package cn.fuzhizhuang.starter.redisson.annotation;

import java.lang.annotation.*;

/**
 * Redisson缓存删除注解
 *
 * @author Fu.zhizhuang
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RedissonCacheEvict {

    /**
     * 缓存key前缀
     */
    String prefix() default "";

    /**
     * 缓存键
     */
    String[] keys() default "";
}
