/**
 * 项目名称:  redisson-plus-spring-boot-starter
 * 公司名称:  YiShoTech
 * All rights Reserved, Designed By YiShoTech 2023-2024
 */
package cn.yishotech.starter.redisson.annotation;

import java.lang.annotation.*;

/**
 * <p>类路径:cn.yishotech.starter.annotation.RedissonCacheEvict</p>
 * <p>类描述:Redisson缓存删除注解</p>
 * <p>创建人:jason zong</p>
 * <p>创建时间:2024/10/05 19:43</p>
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
