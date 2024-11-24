package cn.fuzhizhuang.starter.redisson.aop;

import cn.fuzhizhuang.starter.redisson.annotation.DistributedLock;
import cn.fuzhizhuang.starter.redisson.lock.Lock;
import cn.fuzhizhuang.starter.redisson.lock.factory.LockFactory;
import cn.fuzhizhuang.starter.redisson.model.LockInfo;
import cn.fuzhizhuang.starter.redisson.util.SpelUtil;
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
 * 分布式锁切面
 *
 * @author Fu.zhizhuang
 */
@Slf4j
@Component
@Aspect
public class DistributedLockAspect {
    public static final String LOCK_NAME_KEY = "lock";
    public static final String LOCK_NAME_SEPARATOR = ".";

    @Resource
    private LockFactory lockFactory;

    private static String getBusinessKeyName(DistributedLock distributedLock, ProceedingJoinPoint joinPoint, Method method) {
        String[] keys = distributedLock.keys();
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

    @Around("@annotation(cn.fuzhizhuang.starter.redisson.annotation.DistributedLock)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        // 获取方法
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        // 获取Lock注解信息
        DistributedLock distributedLockAnnotation = method.getAnnotation(DistributedLock.class);
        String lockName = LOCK_NAME_KEY + LOCK_NAME_SEPARATOR + getLockNameKey(distributedLockAnnotation.name(), method) + getBusinessKeyName(distributedLockAnnotation, joinPoint, method);
        log.info("lockName: {}", lockName);
        LockInfo lockInfo = LockInfo.builder()
                .name(lockName)
                .lockType(distributedLockAnnotation.type())
                .waitTime(distributedLockAnnotation.waitTime())
                .leaseTime(distributedLockAnnotation.leaseTime())
                .build();
        Lock lock = lockFactory.getLock(lockInfo);
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

}
