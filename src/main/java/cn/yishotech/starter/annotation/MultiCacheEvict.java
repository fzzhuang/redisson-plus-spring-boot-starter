/**
 * 项目名称:  redisson-plus-spring-boot-starter
 * 公司名称:  YiShoTech
 * All rights Reserved, Designed By YiShoTech 2023-2024
 */
package cn.yishotech.starter.annotation;

import java.lang.annotation.*;

/**
 * <p>类路径:cn.yishotech.starter.annotation.MultiCache</p>
 * <p>类描述:多级缓存删除注解</p>
 * <p>创建人:jason zong</p>
 * <p>创建时间:2024/10/06 22:24</p>
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
    String key() default "";
}
