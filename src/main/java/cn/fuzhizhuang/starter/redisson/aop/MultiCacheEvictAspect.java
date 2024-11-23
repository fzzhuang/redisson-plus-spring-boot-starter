package cn.fuzhizhuang.starter.redisson.aop;

import cn.fuzhizhuang.starter.redisson.annotation.MultiCacheEvict;
import cn.fuzhizhuang.starter.redisson.config.RedissonProperties;
import cn.fuzhizhuang.starter.redisson.multi.impl.MultiCache;
import cn.fuzhizhuang.starter.redisson.util.SpelUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
@Aspect
@Component
public class MultiCacheEvictAspect {

    @Resource
    private RedissonProperties properties;
    @Resource
    private MultiCache multiCache;

    private static String parseKeys(Method method, Object[] args, String[] keys) {
        // key列表
        StringBuilder cacheKey = new StringBuilder();
        for (String key : keys) {
            String parsed = SpelUtil.parseEl(method, args, key);
            if (StringUtils.isBlank(parsed)) {
                return cacheKey.toString();
            }
            cacheKey.append("_").append(parsed);
        }
        return cacheKey.toString();
    }

    @Around("@annotation(cn.fuzhizhuang.starter.redisson.annotation.MultiCacheEvict)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        // 获取注解
        MultiCacheEvict annotation = method.getAnnotation(MultiCacheEvict.class);
        // 获取key
        String key = getCacheKey(method, joinPoint.getArgs(), properties, annotation);
        multiCache.removeValue(annotation.cacheName(), key);
        return joinPoint.proceed();
    }

    private String getCacheKey(Method method, Object[] args, RedissonProperties properties, MultiCacheEvict annotation) {
        String cachePrefix = properties.getCachePrefix();
        String prefix = annotation.prefix();
        String[] keys = annotation.keys();
        String key = parseKeys(method, args, keys);
        // 避免重复判断字符串是否为空
        boolean prefixBlank = StringUtils.isBlank(cachePrefix);
        String combinedPrefix = prefixBlank ? "" : cachePrefix + ":";
        return String.format("%s%s%s", combinedPrefix, prefix, key);
    }
}
