/**
 * 项目名称:  redisson-plus-spring-boot-starter
 * 公司名称:  YiShoTech
 * All rights Reserved, Designed By YiShoTech 2023-2024
 */
package cn.yishotech.starter.multi.impl;

import cn.yishotech.starter.distribute.IDistributeCache;
import cn.yishotech.starter.event.UpdateL1CacheEvent;
import cn.yishotech.starter.model.DataType;
import cn.yishotech.starter.model.OperateType;
import cn.yishotech.starter.model.UpdateCache;
import cn.yishotech.starter.multi.IMultiCache;
import cn.yishotech.starter.subscribe.IMessageQueue;
import com.github.benmanes.caffeine.cache.Cache;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * <p>类路径:cn.yishotech.starter.multi.impl.MultiCache</p>
 * <p>类描述:多级缓存实现</p>
 * <p>创建人:jason zong</p>
 * <p>创建时间:2024/10/06 19:46</p>
 */
@Slf4j
@Component
public class MultiCache implements IMultiCache {

    @Resource
    private IDistributeCache distributeCache;
    @Resource
    private Map<String, Cache<String, Object>> localCacheMap;
    @Resource
    private IMessageQueue messageQueue;


    @Override
    public void setValue(String cacheName, String key, Object value, DataType dataType) {
        // 一级缓存
        Cache<String, Object> cache = localCacheMap.get(cacheName);
        if (Objects.isNull(cache)) {
            throw new RuntimeException(cacheName + " is not exist, please check and configure.");
        }
        cache.put(key, value);
        // 通知更新一级缓存
        updateL1Cache(cacheName, key, value, OperateType.PUT);
        // 二级缓存
        setCacheValue(key, value, dataType, 0, TimeUnit.MINUTES);
    }

    @Override
    public void setValue(String cacheName, String key, Object value, long timeout, DataType dataType) {
        setValue(cacheName, key, value, timeout, TimeUnit.MINUTES, dataType);
    }

    @Override
    public void setValue(String cacheName, String key, Object value, long timeout, TimeUnit unit, DataType dataType) {
        // 一级缓存
        Cache<String, Object> cache = localCacheMap.get(cacheName);
        if (Objects.isNull(cache)) {
            throw new RuntimeException(cacheName + " is not exist, please check and configure.");
        }
        cache.put(key, value);
        // 通知更新一级缓存
        updateL1Cache(cacheName, key, value, OperateType.PUT);
        // 二级缓存
        setCacheValue(key, value, dataType, timeout, unit);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getValue(String cacheName, String key, DataType dataType) {
        // 从一级缓存中查询数据
        Cache<String, Object> cache = localCacheMap.get(cacheName);
        if (Objects.isNull(cache)) {
            throw new RuntimeException(cacheName + " is not exist, please check and configure.");
        }
        Object ifPresent = cache.getIfPresent(key);
        if (Objects.nonNull(ifPresent)) return (T) ifPresent;
        // 从二级缓存获取数据
        Object cacheValue = getCacheValue(key, dataType);
        if (Objects.nonNull(cacheValue)) {
            updateL1Cache(cacheName, key, cacheValue, OperateType.PUT);
        }
        return (T) cacheValue;
    }

    @Override
    public void removeValue(String cacheName, String key) {
        // 一级缓存
        Cache<String, Object> cache = localCacheMap.get(cacheName);
        if (Objects.isNull(cache)) {
            throw new RuntimeException(cacheName + " is not exist, please check and configure.");
        }
        cache.invalidate(key);
        // 通知更新一级缓存
        updateL1Cache(cacheName, key, null, OperateType.EVICT);
        // 二级缓存
        distributeCache.removeValue(key);
    }

    @Override
    public void updateL1Cache(String cacheName, String key, Object value, OperateType operateType) {

        // 封装事件
        UpdateCache updateCache = UpdateCache.builder().cacheName(cacheName).key(key).value(value).operateType(operateType).build();
        UpdateL1CacheEvent event = UpdateL1CacheEvent.create(updateCache);
        // 发送消息
        messageQueue.sendMessage("updateL1Cache", event);
    }

    private Object getCacheValue(String key, DataType dataType) {
        return switch (dataType) {
            case MAP -> distributeCache.getMap(key);
            case LIST -> distributeCache.getList(key);
            case SET -> distributeCache.getSet(key);
            case SORTEDSET -> distributeCache.getSortedSet(key);
            case DEFAULT -> distributeCache.getValue(key);
        };
    }

    private void setCacheValue(String key, Object data, DataType dataType, long timeout, TimeUnit unit) {
        boolean hasTimeout = timeout > 0;
        switch (dataType) {
            case MAP:
                if (hasTimeout) {
                    distributeCache.setMap(key, (Map<?, ?>) data, timeout, unit);
                } else {
                    distributeCache.setMap(key, (Map<?, ?>) data);
                }
                break;
            case SET:
                if (hasTimeout) {
                    distributeCache.setSet(key, (Set<?>) data, timeout, unit);
                } else {
                    distributeCache.setSet(key, (Set<?>) data);
                }
                break;
            case LIST:
                if (hasTimeout) {
                    distributeCache.setList(key, (List<?>) data, timeout, unit);
                } else {
                    distributeCache.setList(key, (List<?>) data);
                }
                break;
            case SORTEDSET:
                if (hasTimeout) {
                    distributeCache.setSortedSet(key, (SortedSet<?>) data, timeout, unit);
                } else {
                    distributeCache.setSortedSet(key, (SortedSet<?>) data);
                }
                break;
            case DEFAULT:
                if (hasTimeout) {
                    distributeCache.setValue(key, data, timeout, unit);
                } else {
                    distributeCache.setValue(key, data);
                }
                break;
        }
    }
}
