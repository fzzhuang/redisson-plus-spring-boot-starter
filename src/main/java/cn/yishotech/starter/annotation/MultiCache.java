/**
 * 项目名称:  redisson-plus-spring-boot-starter
 * 公司名称:  YiShoTech
 * All rights Reserved, Designed By YiShoTech 2023-2024
 */
package cn.yishotech.starter.annotation;

import cn.yishotech.starter.model.DataType;

import java.lang.annotation.*;

/**
 * <p>类路径:cn.yishotech.starter.annotation.MultiCache</p>
 * <p>类描述:多级缓存注解</p>
 * <p>创建人:jason zong</p>
 * <p>创建时间:2024/10/06 22:24</p>
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
     * 数据类型
     */
    DataType type() default DataType.DEFAULT;
}
