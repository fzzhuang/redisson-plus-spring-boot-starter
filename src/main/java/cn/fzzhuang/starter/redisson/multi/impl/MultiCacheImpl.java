package cn.fzzhuang.starter.redisson.multi.impl;

import cn.fzzhuang.starter.redisson.distribute.DistributeCache;
import cn.fzzhuang.starter.redisson.event.UpdateL1CacheEvent;
import cn.fzzhuang.starter.redisson.model.DataType;
import cn.fzzhuang.starter.redisson.model.OperateType;
import cn.fzzhuang.starter.redisson.model.UpdateCache;
import cn.fzzhuang.starter.redisson.multi.MultiCache;
import cn.fzzhuang.starter.redisson.subscribe.IMessageQueue;
import cn.fzzhuang.starter.redisson.util.CacheUtil;
import com.github.benmanes.caffeine.cache.Cache;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 多级缓存实现
 *
 * @author Fu.zhizhuang
 */
@Slf4j
@Component
public class MultiCacheImpl implements MultiCache {

    @Resource
    private DistributeCache distributeCache;
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
        CacheUtil.setDistributedValue(key, value, dataType, 0, TimeUnit.MINUTES, distributeCache);
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
        CacheUtil.setDistributedValue(key, value, dataType, timeout, unit, distributeCache);
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
        Object cacheValue = CacheUtil.getDistributeCacheValue(key, dataType, distributeCache);
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
}
