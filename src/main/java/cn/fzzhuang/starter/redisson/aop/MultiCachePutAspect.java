package cn.fzzhuang.starter.redisson.aop;

import cn.fzzhuang.starter.redisson.annotation.MultiCachePut;
import cn.fzzhuang.starter.redisson.config.MultiCacheProperties;
import cn.fzzhuang.starter.redisson.config.RedissonProperties;
import cn.fzzhuang.starter.redisson.multi.MultiCache;
import cn.fzzhuang.starter.redisson.util.CacheUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * 多级缓存更新切面
 *
 * @author Fu.zhizhuang
 */
@Slf4j
@Aspect
@Component
public class MultiCachePutAspect {

    @Resource
    private RedissonProperties properties;
    @Resource
    private MultiCacheProperties multiCacheProperties;
    @Resource
    private MultiCache multiCache;

    @Around("@annotation(cn.fzzhuang.starter.redisson.annotation.MultiCachePut)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        // 获取注解信息
        MultiCachePut annotation = method.getAnnotation(MultiCachePut.class);
        // 获取缓存key
        String cacheKey = getCacheKey(method, joinPoint.getArgs(), properties, annotation);
        // 删除缓存,更新缓存
        Object value = CacheUtil.parsedValue(annotation.type(), joinPoint.proceed());
        multiCache.removeValue(annotation.cacheName(), cacheKey);
        return CacheUtil.setMultiCacheValue(annotation.cacheName(), cacheKey, value, annotation.expire(), annotation.unit(), annotation.type(), multiCacheProperties.isAllowNullValue(), multiCache);
    }

    private String getCacheKey(Method method, Object[] args, RedissonProperties properties, MultiCachePut annotation) {
        String cachePrefix = properties.getCachePrefix();
        String prefix = annotation.prefix();
        String[] keys = annotation.keys();
        String key = CacheUtil.parseKeys(method, args, keys);
        return CacheUtil.buildCacheKey(cachePrefix, prefix, key);
    }
}

