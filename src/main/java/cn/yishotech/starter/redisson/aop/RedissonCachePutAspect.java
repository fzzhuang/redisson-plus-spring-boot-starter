/**
 * 项目名称:  redisson-plus-spring-boot-starter
 * 公司名称:  YiShoTech
 * All rights Reserved, Designed By YiShoTech 2023-2024
 */
package cn.yishotech.starter.redisson.aop;

import cn.yishotech.starter.redisson.annotation.RedissonCachePut;
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
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.concurrent.TimeUnit;

/**
 * <p>类路径:cn.yishotech.starter.aop.RedissonCachePutAspect</p>
 * <p>类描述:Redisson缓存存储切面</p>
 * <p>创建人:jason zong</p>
 * <p>创建时间:2024/10/06 14:24</p>
 */
@Slf4j
@Component
@Aspect
public class RedissonCachePutAspect {

    @Resource
    private RedissonProperties properties;
    @Resource
    private IDistributeCache distributeCache;

    @Around("@annotation(cn.yishotech.starter.redisson.annotation.RedissonCachePut)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        // 获取注解
        RedissonCachePut annotation = method.getAnnotation(RedissonCachePut.class);
        // 获取缓存key
        String key = getCacheKey(method, joinPoint.getArgs(), properties, annotation);
        // 存储值
        setCacheValue(key, annotation, joinPoint.proceed());
        return joinPoint.proceed();
    }

    private void setCacheValue(String key, RedissonCachePut annotation, Object proceed) {
        DataType type = annotation.type();
        long timeout = annotation.timeout();
        TimeUnit timeUnit = annotation.timeUnit();

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

    private String getCacheKey(Method method, Object[] args, RedissonProperties properties, RedissonCachePut annotation) {
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
            cacheKey.append("_").append(parsed);
        }
        return cacheKey.toString();
    }
}
