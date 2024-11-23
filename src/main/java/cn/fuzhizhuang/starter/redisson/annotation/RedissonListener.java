package cn.fuzhizhuang.starter.redisson.annotation;

import java.lang.annotation.*;

/**
 * Redisson消息监听注解
 *
 * @author Fu.zhizhuang
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
public @interface RedissonListener {

    String topic() default "";
}
