package cn.fzzhuang.starter.redisson.multi.listener;

import cn.fzzhuang.starter.redisson.annotation.RedissonListener;
import cn.fzzhuang.starter.redisson.event.UpdateL1CacheEvent;
import cn.fzzhuang.starter.redisson.model.OperateType;
import cn.fzzhuang.starter.redisson.model.UpdateCache;
import com.alibaba.fastjson2.JSON;
import com.github.benmanes.caffeine.cache.Cache;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.listener.MessageListener;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 更新L1缓存监听器
 *
 * @author Fu.zhizhuang
 */
@Slf4j
@Component
@RedissonListener(topic = "updateL1Cache")
public class UpdateL1CacheListener implements MessageListener<UpdateL1CacheEvent> {

    @Resource
    private Map<String, Cache<String, Object>> localCacheMap;

    @Override
    public void onMessage(CharSequence charSequence, UpdateL1CacheEvent updateCacheEvent) {
        log.debug("接收到更新L1 cache消息, message:{}", JSON.toJSONString(updateCacheEvent));
        UpdateCache updateCache = updateCacheEvent.getData();
        OperateType operateType = updateCache.getOperateType();
        String cacheName = updateCache.getCacheName();
        switch (operateType) {
            case PUT -> localCacheMap.get(cacheName).put(updateCache.getKey(), updateCache.getValue());
            case EVICT -> localCacheMap.get(cacheName).invalidate(updateCache.getKey());
        }
    }
}
