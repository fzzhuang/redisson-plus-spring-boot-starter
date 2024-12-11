package cn.fzzhuang.starter.redisson.aop;

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
import java.util.Objects;

/**
 * 多级缓存查询切面
 *
 * @author Fu.zhizhuang
 */
@Slf4j
@Aspect
@Component
public class MultiCacheAspect {

    @Resource
    private RedissonProperties properties;
    @Resource
    private MultiCacheProperties multiCacheProperties;
    @Resource
    private MultiCache multiCache;


    @Around("@annotation(cn.fzzhuang.starter.redisson.annotation.MultiCache)")
    public Object around(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        // 获取注解信息
        cn.fzzhuang.starter.redisson.annotation.MultiCache annotation = method.getAnnotation(cn.fzzhuang.starter.redisson.annotation.MultiCache.class);
        // 获取缓存key
        String cacheKey = getCacheKey(method, joinPoint.getArgs(), properties, annotation);
        // 获取缓存中数据
        Object value = CacheUtil.parsedValue(annotation.type(), multiCache.getValue(annotation.cacheName(), cacheKey, annotation.type()));
        // 缓存中有数据，直接返回
        if (Objects.nonNull(value)) return value;
        try {
            Object proceed = joinPoint.proceed();
            return CacheUtil.setMultiCacheValue(annotation.cacheName(), cacheKey, proceed, annotation.expire(), annotation.unit(), annotation.type(), multiCacheProperties.isAllowNullValue(), multiCache);
        } catch (Throwable e) {
            log.error("MultiCacheAspect error", e);
            throw new RuntimeException(e);
        }
    }

    private String getCacheKey(Method method, Object[] args, RedissonProperties properties, cn.fzzhuang.starter.redisson.annotation.MultiCache annotation) {
        String cachePrefix = properties.getCachePrefix();
        String prefix = annotation.prefix();
        String[] keys = annotation.keys();
        String key = CacheUtil.parseKeys(method, args, keys);
        return CacheUtil.buildCacheKey(cachePrefix, prefix, key);
    }
}
