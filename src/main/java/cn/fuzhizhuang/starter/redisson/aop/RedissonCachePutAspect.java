package cn.fuzhizhuang.starter.redisson.aop;

import cn.fuzhizhuang.starter.redisson.annotation.RedissonCachePut;
import cn.fuzhizhuang.starter.redisson.config.RedissonProperties;
import cn.fuzhizhuang.starter.redisson.distribute.DistributeCache;
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
 * Redisson更新切面
 *
 * @author Fu.zhizhuang
 */
@Slf4j
@Aspect
@Component
public class RedissonCachePutAspect {

    @Resource
    private DistributeCache distributeCache;
    @Resource
    private RedissonProperties properties;

    @Around("@annotation(cn.fuzhizhuang.starter.redisson.annotation.RedissonCachePut)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        // 获取注解信息
        RedissonCachePut annotation = method.getAnnotation(RedissonCachePut.class);
        // 获取缓存key
        String cacheKey = getCacheKey(method, joinPoint.getArgs(), properties, annotation);
        Object value = CacheUtil.parsedValue(annotation.type(), joinPoint.proceed());
        distributeCache.removeValue(cacheKey);
        CacheUtil.setDistributedValue(cacheKey, value, annotation.type(), annotation.expire(), annotation.unit(), distributeCache);
        return value;
    }

    private String getCacheKey(Method method, Object[] args, RedissonProperties properties, RedissonCachePut annotation) {
        String cachePrefix = properties.getCachePrefix();
        String prefix = annotation.prefix();
        String[] keys = annotation.keys();
        String key = CacheUtil.parseKeys(method, args, keys);
        return CacheUtil.buildCacheKey(cachePrefix, prefix, key);
    }
}
