/**
 * 项目名称:  redisson-plus-spring-boot-starter
 * 公司名称:  YiShoTech
 * All rights Reserved, Designed By YiShoTech 2023-2024
 */
package cn.yishotech.starter.aop;

import cn.yishotech.starter.annotation.MultiCacheEvict;
import cn.yishotech.starter.config.RedissonProperties;
import cn.yishotech.starter.multi.impl.MultiCache;
import cn.yishotech.starter.util.SpelUtil;
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
 * <p>类路径:cn.yishotech.starter.aop.MultiCacheEvictAspect</p>
 * <p>类描述:多级缓存删除切面</p>
 * <p>创建人:jason zong</p>
 * <p>创建时间:2024/10/06 22:50</p>
 */
@Slf4j
@Aspect
@Component
public class MultiCacheEvictAspect {

    @Resource
    private RedissonProperties properties;
    @Resource
    private MultiCache multiCache;

    @Around("@annotation(cn.yishotech.starter.annotation.MultiCacheEvict)")
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
        return String.format("%s%s_%s", combinedPrefix, prefix, key);
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
