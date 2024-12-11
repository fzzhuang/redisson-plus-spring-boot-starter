package cn.fzzhuang.starter.redisson.aop;

import cn.fzzhuang.starter.redisson.annotation.RedissonCacheEvict;
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

/**
 * Redisson删除切面
 *
 * @author Fu.zhizhuang
 */
@Slf4j
@Aspect
@Component
public class RedissonCacheEvictAspect {

    @Resource
    private DistributeCache distributeCache;
    @Resource
    private RedissonProperties properties;

    @Around("@annotation(cn.fzzhuang.starter.redisson.annotation.RedissonCacheEvict)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        // 获取注解信息
        RedissonCacheEvict annotation = method.getAnnotation(RedissonCacheEvict.class);
        // 获取缓存key
        String cacheKey = getCacheKey(method, joinPoint.getArgs(), properties, annotation);
        // 删除缓存
        distributeCache.removeValue(cacheKey);
        return joinPoint.proceed();
    }

    private String getCacheKey(Method method, Object[] args, RedissonProperties properties, RedissonCacheEvict annotation) {
        String cachePrefix = properties.getCachePrefix();
        String prefix = annotation.prefix();
        String[] keys = annotation.keys();
        String key = CacheUtil.parseKeys(method, args, keys);
        return CacheUtil.buildCacheKey(cachePrefix, prefix, key);
    }
}
