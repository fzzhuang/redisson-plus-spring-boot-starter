/**
 * 项目名称:  redisson-plus-spring-boot-starter
 * 公司名称:  YiShoTech
 * All rights Reserved, Designed By YiShoTech 2023-2024
 */
package cn.yishotech.starter.annotation;

import cn.yishotech.starter.model.DataType;

import java.lang.annotation.*;

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
    String key() default "";

    /**
     * 查询数据类型
     */
    DataType type() default DataType.DEFAULT;
}
