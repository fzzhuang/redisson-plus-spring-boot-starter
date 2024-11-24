package cn.fuzhizhuang.starter.redisson.aop;

import cn.fuzhizhuang.starter.redisson.annotation.MultiCacheEvict;
import cn.fuzhizhuang.starter.redisson.config.RedissonProperties;
import cn.fuzhizhuang.starter.redisson.multi.MultiCache;
import cn.fuzhizhuang.starter.redisson.util.CacheUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * 多级缓存删除切面
 *
 * @author Fu.zhizhuang
 */
@Slf4j
@Component
@Aspect
public class MultiCacheEvictAspect {

    @Resource
    private MultiCache multiCache;
    @Resource
    private RedissonProperties redissonProperties;

    @Around("@annotation(cn.fuzhizhuang.starter.redisson.annotation.MultiCacheEvict)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        // 获取注解
        MultiCacheEvict annotation = method.getAnnotation(MultiCacheEvict.class);
        // 获取key
        String cacheKey = getCacheKey(method, joinPoint.getArgs(), redissonProperties, annotation);
        // 删除缓存
        multiCache.removeValue(annotation.cacheName(), cacheKey);
        return joinPoint.proceed();
    }

    private String getCacheKey(Method method, Object[] args, RedissonProperties properties, MultiCacheEvict annotation) {
        String cachePrefix = properties.getCachePrefix();
        String prefix = annotation.prefix();
        String[] keys = annotation.keys();
        String key = CacheUtil.parseKeys(method, args, keys);
        return CacheUtil.buildCacheKey(cachePrefix, prefix, key);
    }
}
