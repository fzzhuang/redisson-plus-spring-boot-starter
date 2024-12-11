package cn.fzzhuang.starter.redisson.aop;

import cn.fzzhuang.starter.redisson.annotation.RedissonCache;
import cn.fzzhuang.starter.redisson.config.RedissonProperties;
import cn.fzzhuang.starter.redisson.distribute.DistributeCache;
import cn.fzzhuang.starter.redisson.util.CacheUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Objects;

/**
 * 分布式缓存切面
 *
 * @author Fu.zhizhuang
 */
@Slf4j
@Component
@Aspect
public class RedissonCacheAspect {

    @Resource
    private RedissonProperties properties;
    @Resource
    private DistributeCache distributeCache;

    @Around("@annotation(cn.fzzhuang.starter.redisson.annotation.RedissonCache)")
    public Object around(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        // 获取注解信息
        RedissonCache annotation = method.getAnnotation(RedissonCache.class);
        // 获取缓存key
        String cacheKey = getCacheKey(method, joinPoint.getArgs(), properties, annotation);
        // 获取缓存中数据
        Object value = CacheUtil.getDistributeCacheValue(cacheKey, annotation.type(), distributeCache);
        // 缓存中有数据，直接返回
        if (Objects.nonNull(value)) return value;
        try {
            Object proceed = joinPoint.proceed();
            CacheUtil.setDistributedValue(cacheKey, proceed, annotation.type(), annotation.expire(), annotation.unit(), distributeCache);
            return proceed;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private String getCacheKey(Method method, Object[] args, RedissonProperties properties, RedissonCache annotation) {
        String cachePrefix = properties.getCachePrefix();
        String prefix = annotation.prefix();
        String[] keys = annotation.keys();
        String key = CacheUtil.parseKeys(method, args, keys);
        return CacheUtil.buildCacheKey(cachePrefix, prefix, key);
    }
}
