/**
 * 项目名称:  redisson-plus-spring-boot-starter
 * 公司名称:  YiShoTech
 * All rights Reserved, Designed By YiShoTech 2023-2024
 */
package cn.yishotech.starter.redisson.annotation;

import java.lang.annotation.*;

/**
 * <p>类路径:cn.yishotech.starter.annotation.RedissonListener</p>
 * <p>类描述:Redisson消息监听注解</p>
 * <p>创建人:jason zong</p>
 * <p>创建时间:2024/10/06 18:22</p>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
public @interface RedissonListener {

    String topic() default "";
}
