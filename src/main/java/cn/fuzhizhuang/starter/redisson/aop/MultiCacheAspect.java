package cn.fuzhizhuang.starter.redisson.aop;

import cn.fuzhizhuang.starter.redisson.annotation.MultiCache;
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

    @Around("@annotation(cn.fuzhizhuang.starter.redisson.annotation.MultiCache)")
    public Object around(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        // 获取注解
        MultiCache annotation = method.getAnnotation(MultiCache.class);
        // 获取缓存key
        String key = getCacheKey(method, joinPoint.getArgs(), properties, annotation);
        Object value = getCacheValue(annotation.cacheName(), key, annotation.type());
        // 如果缓存中有数据，则直接返回
        if (Objects.nonNull(value)) return value;
        try {
            // 执行方法，并将结果放入缓存
            value = joinPoint.proceed();
            // 列表类型不允许为空
            value = parsedValue(annotation.type(), value);
            // 缓存中不允许为空值
            boolean allowNullValue = multiCacheProperties.isAllowNullValue();
            // 缓存中允许为空值
            if (allowNullValue || Objects.nonNull(value)) {
                multiCache.setValue(annotation.cacheName(), key, value, annotation.expire(), annotation.unit(), annotation.type());
            }
            return value;
        } catch (Throwable e) {
            log.error("更新多级缓存失败", e);
            throw new RuntimeException(e);
        }
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

    private String getCacheKey(Method method, Object[] args, RedissonProperties properties, MultiCache annotation) {
        String cachePrefix = properties.getCachePrefix();
        String prefix = annotation.prefix();
        String[] keys = annotation.keys();
        String key = parseKeys(method, args, keys);
        // 避免重复判断字符串是否为空
        boolean prefixBlank = StringUtils.isBlank(cachePrefix);
        String combinedPrefix = prefixBlank ? "" : cachePrefix + ":";
        return String.format("%s%s%s", combinedPrefix, prefix, key);
    }

    private Object getCacheValue(String cacheName, String key, DataType dataType) {
        Object value = multiCache.getValue(cacheName, key, dataType);
        if (DataType.LIST.equals(dataType)) {
            List<?> list = (List<?>) value;
            if (!list.isEmpty()) return new ArrayList<>(list);
        } else if (DataType.SET.equals(dataType)) {
            Set<?> set = (Set<?>) value;
            if (!set.isEmpty()) return set;
        } else if (DataType.SORTEDSET.equals(dataType)) {
            SortedSet<?> sortedSet = (SortedSet<?>) value;
            if (!sortedSet.isEmpty()) return sortedSet;
        } else if (DataType.MAP.equals(dataType)) {
            Map<?, ?> map = (Map<?, ?>) value;
            if (!map.isEmpty()) return map;
        } else if (DataType.DEFAULT.equals(dataType)) {
            if (Objects.nonNull(value)) return value;
        }
        return null;
    }
}
