package cn.fuzhizhuang.starter.redisson.aop;

import cn.fuzhizhuang.starter.redisson.annotation.MultiCachePut;
import cn.fuzhizhuang.starter.redisson.config.MultiCacheProperties;
import cn.fuzhizhuang.starter.redisson.config.RedissonProperties;
import cn.fuzhizhuang.starter.redisson.model.DataType;
import cn.fuzhizhuang.starter.redisson.multi.IMultiCache;
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
import java.util.*;

/**
 * 多级缓存存储切面
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
    private IMultiCache multiCache;

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

    @Around("@annotation(cn.fuzhizhuang.starter.redisson.annotation.MultiCachePut)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        // 获取注解
        MultiCachePut annotation = method.getAnnotation(MultiCachePut.class);
        // 获取缓存key
        String key = getCacheKey(method, joinPoint.getArgs(), properties, annotation);
        // 更新缓存
        Object value = parsedValue(annotation.type(), joinPoint.proceed());
        if (Objects.nonNull(value) || multiCacheProperties.isAllowNullValue()) {
            multiCache.setValue(annotation.cacheName(), key, joinPoint.proceed(), annotation.expire(), annotation.timeUnit(), annotation.type());
        }
        return joinPoint.proceed();
    }

    private String getCacheKey(Method method, Object[] args, RedissonProperties properties, MultiCachePut annotation) {
        String cachePrefix = properties.getCachePrefix();
        String prefix = annotation.prefix();
        String[] keys = annotation.keys();
        String key = parseKeys(method, args, keys);
        // 避免重复判断字符串是否为空
        boolean prefixBlank = StringUtils.isBlank(cachePrefix);
        String combinedPrefix = prefixBlank ? "" : cachePrefix + ":";
        return String.format("%s%s%s", combinedPrefix, prefix, key);
    }

    private Object parsedValue(DataType type, Object value) {
        if (DataType.LIST.equals(type)) {
            List<?> list = (List<?>) value;
            if (!list.isEmpty()) return list;
        } else if (DataType.MAP.equals(type)) {
            Map<?, ?> map = (Map<?, ?>) value;
            if (!map.isEmpty()) return map;
        } else if (DataType.SET.equals(type)) {
            Set<?> set = (Set<?>) value;
            if (!set.isEmpty()) return set;
        } else if (DataType.SORTEDSET.equals(type)) {
            SortedSet<?> sortedSet = (SortedSet<?>) value;
            if (!sortedSet.isEmpty()) return sortedSet;
        } else {
            if (Objects.nonNull(value)) return value;
        }
        return null;
    }
}
