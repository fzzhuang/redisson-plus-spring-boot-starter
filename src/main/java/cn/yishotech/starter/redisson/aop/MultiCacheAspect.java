/**
 * 项目名称:  redisson-plus-spring-boot-starter
 * 公司名称:  YiShoTech
 * All rights Reserved, Designed By YiShoTech 2023-2024
 */
package cn.yishotech.starter.redisson.aop;

import cn.yishotech.starter.redisson.annotation.MultiCache;
import cn.yishotech.starter.redisson.config.MultiCacheProperties;
import cn.yishotech.starter.redisson.config.RedissonProperties;
import cn.yishotech.starter.redisson.model.DataType;
import cn.yishotech.starter.redisson.multi.IMultiCache;
import cn.yishotech.starter.redisson.util.SpelUtil;
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
 * <p>类路径:cn.yishotech.starter.aop.MultiCacheAspect</p>
 * <p>类描述:多级缓存查询切面</p>
 * <p>创建人:jason zong</p>
 * <p>创建时间:2024/10/06 22:38</p>
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

    @Around("@annotation(cn.yishotech.starter.redisson.annotation.MultiCache)")
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
