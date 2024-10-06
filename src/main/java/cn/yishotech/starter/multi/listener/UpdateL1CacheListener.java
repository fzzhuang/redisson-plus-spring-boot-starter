/**
 * 项目名称:  redisson-plus-spring-boot-starter
 * 公司名称:  YiShoTech
 * All rights Reserved, Designed By YiShoTech 2023-2024
 */
package cn.yishotech.starter.multi.listener;

import cn.yishotech.starter.annotation.RedissonListener;
import cn.yishotech.starter.model.Event;
import cn.yishotech.starter.model.OperateType;
import cn.yishotech.starter.model.UpdateCache;
import com.alibaba.fastjson2.JSON;
import com.github.benmanes.caffeine.cache.Cache;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.listener.MessageListener;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * <p>类路径:cn.yishotech.starter.multi.listener.UpdateL1CacheListener</p>
 * <p>类描述:更新L1缓存监听器</p>
 * <p>创建人:jason zong</p>
 * <p>创建时间:2024/10/06 23:04</p>
 */
@Slf4j
@Component
@RedissonListener(topic = "updateL1Cache")
public class UpdateL1CacheListener implements MessageListener<Event<UpdateCache>> {

    @Resource
    private Map<String, Cache<String, Object>> localCacheMap;

    @Override
    public void onMessage(CharSequence charSequence, Event<UpdateCache> updateCacheEvent) {
        log.info("接收到更新L1 cache消息, message:{}", JSON.toJSONString(updateCacheEvent));
        UpdateCache updateCache = updateCacheEvent.getData();
        OperateType operateType = updateCache.getOperateType();
        String cacheName = updateCache.getCacheName();
        switch (operateType) {
            case PUT -> localCacheMap.get(cacheName).put(updateCache.getKey(), updateCache.getValue());
            case EVICT -> localCacheMap.get(cacheName).invalidate(updateCache.getKey());
        }
    }
}
