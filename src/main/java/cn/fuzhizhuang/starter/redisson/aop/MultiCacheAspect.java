package cn.fuzhizhuang.starter.redisson.aop;

import cn.fuzhizhuang.starter.redisson.config.MultiCacheProperties;
import cn.fuzhizhuang.starter.redisson.config.RedissonProperties;
import cn.fuzhizhuang.starter.redisson.multi.MultiCache;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Objects;

import static cn.fuzhizhuang.starter.redisson.util.CacheUtil.*;

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


    @Around("@annotation(cn.fuzhizhuang.starter.redisson.annotation.MultiCache)")
    public Object around(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        // 获取注解信息
        cn.fuzhizhuang.starter.redisson.annotation.MultiCache annotation = method.getAnnotation(cn.fuzhizhuang.starter.redisson.annotation.MultiCache.class);
        // 获取缓存key
        String cacheKey = getCacheKey(method, joinPoint.getArgs(), properties, annotation);
        // 获取缓存中数据
        Object value = parsedValue(annotation.type(), multiCache.getValue(annotation.cacheName(), cacheKey, annotation.type()));
        // 缓存中有数据，直接返回
        if (Objects.nonNull(value)) return value;
        try {
            Object proceed = joinPoint.proceed();
             return setMultiCacheValue(annotation.cacheName(), cacheKey, proceed, annotation.expire(), annotation.unit(), annotation.type(), multiCacheProperties.isAllowNullValue(), multiCache);
        } catch (Throwable e) {
            log.error("MultiCacheAspect error", e);
            throw new RuntimeException(e);
        }
    }

    private String getCacheKey(Method method, Object[] args, RedissonProperties properties, cn.fuzhizhuang.starter.redisson.annotation.MultiCache annotation) {
        String cachePrefix = properties.getCachePrefix();
        String prefix = annotation.prefix();
        String[] keys = annotation.keys();
        String key = parseKeys(method, args, keys);
        return buildCacheKey(cachePrefix, prefix, key);
    }
}
