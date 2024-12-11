package cn.fzzhuang.starter.redisson.annotation;

import java.lang.annotation.*;

/**
 * 多级缓存删除注解
 *
 * @author Fu.zhizhuang
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MultiCacheEvict {

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
}
