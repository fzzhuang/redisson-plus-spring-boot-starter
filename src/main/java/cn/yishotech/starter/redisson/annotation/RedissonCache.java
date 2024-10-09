/**
 * 项目名称:  redisson-plus-spring-boot-starter
 * 公司名称:  YiShoTech
 * All rights Reserved, Designed By YiShoTech 2023-2024
 */
package cn.yishotech.starter.redisson.annotation;

import cn.yishotech.starter.redisson.model.DataType;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * <p>类路径:cn.yishotech.starter.annotation.RedissonCache</p>
 * <p>类描述:Redisson缓存注解</p>
 * <p>创建人:jason zong</p>
 * <p>创建时间:2024/10/05 19:40</p>
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
