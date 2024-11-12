/**
 * 项目名称:  redisson-plus-spring-boot-starter
 * 公司名称:  YiShoTech
 * All rights Reserved, Designed By YiShoTech 2023-2024
 */
package cn.yishotech.starter.redisson.aop;

import cn.yishotech.starter.redisson.annotation.MultiCachePut;
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
 * <p>类路径:cn.yishotech.starter.aop.MultiCachePut</p>
 * <p>类描述:多级缓存存储切面</p>
 * <p>创建人:jason zong</p>
 * <p>创建时间:2024/10/06 22:42</p>
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


    @Around("@annotation(cn.yishotech.starter.redisson.annotation.MultiCachePut)")
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
}
