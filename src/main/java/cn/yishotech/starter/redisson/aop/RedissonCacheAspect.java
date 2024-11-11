/**
 * 项目名称:  redisson-plus-spring-boot-starter
 * 公司名称:  YiShoTech
 * All rights Reserved, Designed By YiShoTech 2023-2024
 */
package cn.yishotech.starter.redisson.aop;

import cn.yishotech.starter.redisson.annotation.RedissonCache;
import cn.yishotech.starter.redisson.config.RedissonProperties;
import cn.yishotech.starter.redisson.distribute.IDistributeCache;
import cn.yishotech.starter.redisson.model.DataType;
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
import java.util.concurrent.TimeUnit;

/**
 * <p>类路径:cn.yishotech.starter.aop.RedissonCacheAspect</p>
 * <p>类描述:Redisson缓存切面'</p>
 * <p>创建人:jason zong</p>
 * <p>创建时间:2024/10/05 19:45</p>
 */
@Slf4j
@Component
@Aspect
public class RedissonCacheAspect {

    @Resource
    private RedissonProperties properties;
    @Resource
    private IDistributeCache distributeCache;

    @Around("@annotation(cn.yishotech.starter.redisson.annotation.RedissonCache)")
    public Object around(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        // 获取注解
        RedissonCache annotation = method.getAnnotation(RedissonCache.class);
        // 获取缓存key
        String key = getCacheKey(method, joinPoint.getArgs(), properties, annotation);
        DataType type = annotation.type();
        // 查询缓存
        Object cacheValue = getValue(key, type);
        if (Objects.nonNull(cacheValue)) return cacheValue;
        try {
            Object result = joinPoint.proceed();
            // 缓存结果
            long expire = annotation.expire();
            setCacheValue(key, result, type, expire, annotation.unit());
            return result;
        } catch (Throwable e) {
            log.error("更新缓存失败", e);
            throw new RuntimeException(e);
        }
    }

    private Object getCacheValue(String key, DataType type) {
        return switch (type) {
            case MAP -> distributeCache.getMap(key);
            case SET -> distributeCache.getSet(key);
            case LIST -> distributeCache.getList(key);
            case SORTEDSET -> distributeCache.getSortedSet(key);
            case DEFAULT -> distributeCache.getValue(key);
        };
    }

    private void setCacheValue(String key, Object proceed, DataType type, long timeout, TimeUnit timeUnit) {
        boolean hasTimeout = timeout > 0;
        switch (type) {
            case MAP:
                if (hasTimeout) {
                    distributeCache.setMap(key, (Map<?, ?>) proceed, timeout, timeUnit);
                } else {
                    distributeCache.setMap(key, (Map<?, ?>) proceed);
                }
                break;
            case SET:
                if (hasTimeout) {
                    distributeCache.setSet(key, (Set<?>) proceed, timeout, timeUnit);
                } else {
                    distributeCache.setSet(key, (Set<?>) proceed);
                }
                break;
            case LIST:
                if (hasTimeout) {
                    distributeCache.setList(key, (List<?>) proceed, timeout, timeUnit);
                } else {
                    distributeCache.setList(key, (List<?>) proceed);
                }
                break;
            case SORTEDSET:
                if (hasTimeout) {
                    distributeCache.setSortedSet(key, (SortedSet<?>) proceed, timeout, timeUnit);
                } else {
                    distributeCache.setSortedSet(key, (SortedSet<?>) proceed);
                }
                break;
            case DEFAULT:
                if (hasTimeout) {
                    distributeCache.setValue(key, proceed, timeout, timeUnit);
                } else {
                    distributeCache.setValue(key, proceed);
                }
                break;
        }
    }

    private String getCacheKey(Method method, Object[] args, RedissonProperties properties, RedissonCache annotation) {
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

    private Object getValue(String key, DataType dataType) {
        Object value = getCacheValue(key, dataType);
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
