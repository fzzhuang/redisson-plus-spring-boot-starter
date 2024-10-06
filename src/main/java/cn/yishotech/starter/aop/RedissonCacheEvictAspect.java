/**
 * 项目名称:  redisson-plus-spring-boot-starter
 * 公司名称:  YiShoTech
 * All rights Reserved, Designed By YiShoTech 2023-2024
 */
package cn.yishotech.starter.aop;

import cn.yishotech.starter.annotation.RedissonCacheEvict;
import cn.yishotech.starter.config.RedissonProperties;
import cn.yishotech.starter.distribute.IDistributeCache;
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
 * <p>类路径:cn.yishotech.starter.aop.RedissonCacheEvictAspect</p>
 * <p>类描述:Redisson缓存删除切面</p>
 * <p>创建人:jason zong</p>
 * <p>创建时间:2024/10/06 14:37</p>
 */
@Slf4j
@Component
@Aspect
public class RedissonCacheEvictAspect {

    @Resource
    private RedissonProperties properties;
    @Resource
    private IDistributeCache distributeCache;

    @Around("@annotation(cn.yishotech.starter.annotation.RedissonCacheEvict)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        // 获取注解
        RedissonCacheEvict annotation = method.getAnnotation(RedissonCacheEvict.class);
        // 获取缓存key
        String key = getCacheKey(method, joinPoint.getArgs(), properties, annotation);
        // 删除缓存
        distributeCache.removeValue(key);
        return joinPoint.proceed();
    }

    private String getCacheKey(Method method, Object[] args, RedissonProperties properties, RedissonCacheEvict annotation) {
        String cachePrefix = properties.getCachePrefix();
        String prefix = annotation.prefix();
        String key = annotation.key();
        key = SpelUtil.parseEl(method, args, key);
        // 避免重复判断字符串是否为空
        boolean prefixBlank = StringUtils.isBlank(cachePrefix);
        String combinedPrefix = prefixBlank ? "" : cachePrefix + ":";
        return String.format("%s%s_%s", combinedPrefix, prefix, key);
    }
}
