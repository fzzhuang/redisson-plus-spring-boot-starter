/**
 * 项目名称:  redisson-plus-spring-boot-starter
 * 公司名称:  YiShoTech
 * All rights Reserved, Designed By YiShoTech 2023-2024
 */
package cn.yishotech.starter.redisson.aop;

import cn.yishotech.starter.redisson.annotation.Lock;
import cn.yishotech.starter.redisson.lock.ILock;
import cn.yishotech.starter.redisson.lock.factory.LockFactory;
import cn.yishotech.starter.redisson.model.LockInfo;
import cn.yishotech.starter.redisson.util.SpelUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * <p>类路径:cn.yishotech.starter.aop.LockAspect</p>
 * <p>类描述:分布式锁切面</p>
 * <p>创建人:jason zong</p>
 * <p>创建时间:2024/10/05 00:08</p>
 */
@Slf4j
@Component
@Aspect
public class LockAspect {
    public static final String LOCK_NAME_KEY = "lock";
    public static final String LOCK_NAME_SEPARATOR = ".";

    @Resource
    private LockFactory lockFactory;

    @Around("@annotation(cn.yishotech.starter.redisson.annotation.Lock)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        // 获取方法
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        // 获取Lock注解信息
        Lock lockAnnotation = method.getAnnotation(Lock.class);
        String lockName = LOCK_NAME_KEY + LOCK_NAME_SEPARATOR + getLockNameKey(lockAnnotation.name(), method) + getBusinessKeyName(lockAnnotation, joinPoint, method);
        log.info("lockName: {}", lockName);
        LockInfo lockInfo = LockInfo.builder()
                .name(lockName)
                .lockType(lockAnnotation.type())
                .waitTime(lockAnnotation.waitTime())
                .leaseTime(lockAnnotation.leaseTime())
                .build();
        ILock lock = lockFactory.getLock(lockInfo);
        boolean acquired = false;
        try {
            acquired = lock.acquire();
            if (acquired) {
                return joinPoint.proceed();
            } else {
                log.info("lockName:{} acquire lock failed", lockName);
                throw new RuntimeException("acquire lock failed, please try again later.");
            }
        } finally {
            if (acquired) {
                // 释放锁
                lock.release();
            }
        }
    }

    private static String getBusinessKeyName(Lock lock, ProceedingJoinPoint joinPoint, Method method) {
        String[] keys = lock.keys();
        List<String> keyList = parseKeys(joinPoint, keys, method);
        // 业务key名
        return getKeyName(keyList);
    }

    private static String getKeyName(List<String> keyList) {
        StringBuilder keyName = new StringBuilder();
        for (String key : keyList) {
            keyName.append(LOCK_NAME_SEPARATOR).append(key);
        }
        return keyName.toString();
    }

    private static List<String> parseKeys(ProceedingJoinPoint joinPoint, String[] keys, Method method) {
        // key列表
        List<String> keyList = new ArrayList<>();
        for (String key : keys) {
            String parsed = SpelUtil.parseEl(method, joinPoint.getArgs(), key);
            keyList.add(parsed);
        }
        return keyList;
    }

    private static String getLockNameKey(String name, Method method) {
        if (Objects.nonNull(name) && !name.isEmpty()) {
            return name;
        } else {
            return SpelUtil.getMethodKey(method);
        }
    }

}
