package cn.fuzhizhuang.starter.redisson.distribute.impl;

import cn.fuzhizhuang.starter.redisson.config.RedissonProperties;
import cn.fuzhizhuang.starter.redisson.distribute.DistributeCache;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.*;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import static cn.fuzhizhuang.starter.redisson.util.CacheUtil.parseTimeUnit;

/**
 * Redisson分布式缓存实现
 *
 * @author Fu.zhizhuang
 */
@Slf4j
@Service
public class RedissonDistributeCache implements DistributeCache {

    @Resource
    private RedissonClient redissonClient;
    @Resource
    private RedissonProperties properties;

    @Override
    public void setValue(String key, Object value) {
        if (properties.isAllowNullValue() || Objects.nonNull(value)) {
            redissonClient.getBucket(key).set(value);
        }
    }

    @Override
    public void setValue(String key, Object value, long expire) {
        if (properties.isAllowNullValue() || Objects.nonNull(value)) {
            redissonClient.getBucket(key).set(value, Duration.ofSeconds(expire));
        }
    }

    @Override
    public void setValue(String key, Object value, long expire, TimeUnit timeUnit) {
        if (properties.isAllowNullValue() || Objects.nonNull(value)) {
            redissonClient.getBucket(key).set(value, parseTimeUnit(expire, timeUnit));
        }
    }

    @Override
    public <T> T getValue(String key) {
        return redissonClient.<T>getBucket(key).get();
    }

    @Override
    public Long incrementValue(String key) {
        return redissonClient.getAtomicLong(key).incrementAndGet();
    }

    @Override
    public Long incrementValue(String key, long delta) {
        return redissonClient.getAtomicLong(key).addAndGet(delta);
    }

    @Override
    public Long decrementValue(String key) {
        return redissonClient.getAtomicLong(key).decrementAndGet();
    }

    @Override
    public Long decrementValue(String key, long delta) {
        return redissonClient.getAtomicLong(key).addAndGet(-delta);
    }

    @Override
    public void removeValue(String key) {
        redissonClient.getBucket(key).delete();
    }

    @Override
    public <K, V> void setMap(String key, Map<K, V> map, long expire) {
        if (properties.isAllowNullValue() || !map.isEmpty()) {
            RMap<K, V> rMap = redissonClient.getMap(key);
            rMap.putAll(map);
            rMap.expire(Duration.ofMinutes(expire));
        }
    }

    @Override
    public <K, V> void setMap(String key, Map<K, V> map, long expire, TimeUnit timeUnit) {
        if (properties.isAllowNullValue() || !map.isEmpty()) {
            RMap<K, V> rMap = redissonClient.getMap(key);
            rMap.putAll(map);
            rMap.expire(parseTimeUnit(expire, timeUnit));
        }
    }

    @Override
    public <K, V> void setMap(String key, Map<K, V> map) {
        if (properties.isAllowNullValue() || !map.isEmpty()) {
            redissonClient.getMap(key).putAll(map);
        }
    }

    @Override
    public <K, V> Map<K, V> getMap(String key) {
        RMap<K, V> map = redissonClient.getMap(key);
        // 转换为Map
        return new ConcurrentHashMap<>(map);
    }

    @Override
    public <K, V> V getMapValue(String key, K mapKey) {
        RMap<K, V> rMap = redissonClient.getMap(key);
        return rMap.get(mapKey);
    }

    @Override
    public <T> void setList(String key, List<T> list) {
        if (properties.isAllowNullValue() || !list.isEmpty()) {
            redissonClient.getList(key).addAll(list);
        }
    }

    @Override
    public <T> void setList(String key, List<T> list, long expire) {
        if (properties.isAllowNullValue() || !list.isEmpty()) {
            RList<T> rList = redissonClient.getList(key);
            rList.addAll(list);
            rList.expire(Duration.ofMinutes(expire));
        }
    }

    @Override
    public <T> void setList(String key, List<T> list, long expire, TimeUnit timeUnit) {
        if (properties.isAllowNullValue() || !list.isEmpty()) {
            RList<T> rList = redissonClient.getList(key);
            rList.addAll(list);
            rList.expire(parseTimeUnit(expire, timeUnit));
        }
    }

    @Override
    public <T> List<T> getList(String key) {
        RList<T> list = redissonClient.getList(key);
        return new ArrayList<>(list);
    }

    @Override
    public <T> void setSet(String key, Set<T> set) {
        if (properties.isAllowNullValue() || !set.isEmpty()) {
            RSet<T> rSet = redissonClient.getSet(key);
            rSet.addAll(set);
        }
    }

    @Override
    public <T> void setSet(String key, Set<T> set, long expire) {
        if (properties.isAllowNullValue() || !set.isEmpty()) {
            RSet<T> rSet = redissonClient.getSet(key);
            rSet.addAll(set);
            rSet.expire(Duration.ofMinutes(expire));
        }
    }

    @Override
    public <T> void setSet(String key, Set<T> set, long expire, TimeUnit timeUnit) {
        if (properties.isAllowNullValue() || !set.isEmpty()) {
            RSet<T> rSet = redissonClient.getSet(key);
            rSet.addAll(set);
            rSet.expire(parseTimeUnit(expire, timeUnit));
        }
    }

    @Override
    public <T> Set<T> getSet(String key) {
        RSet<T> set = redissonClient.getSet(key);
        return new HashSet<>(set);
    }

    @Override
    public <T> void setSortedSet(String key, SortedSet<T> sortedSet) {
        if (properties.isAllowNullValue() || !sortedSet.isEmpty()) {
            RSortedSet<T> rSortedSet = redissonClient.getSortedSet(key);
            rSortedSet.addAll(sortedSet);
        }
    }

    @Override
    public <T> void setSortedSet(String key, SortedSet<T> sortedSet, long expire) {
        if (properties.isAllowNullValue() || !sortedSet.isEmpty()) {
            RSortedSet<T> rSortedSet = redissonClient.getSortedSet(key);
            rSortedSet.addAll(sortedSet);
            rSortedSet.expire(Duration.ofMinutes(expire));
        }
    }

    @Override
    public <T> void setSortedSet(String key, SortedSet<T> sortedSet, long expire, TimeUnit timeUnit) {
        if (properties.isAllowNullValue() || !sortedSet.isEmpty()) {
            RSortedSet<T> rSortedSet = redissonClient.getSortedSet(key);
            rSortedSet.addAll(sortedSet);
            rSortedSet.expire(parseTimeUnit(expire, timeUnit));
        }
    }

    @Override
    public <T> SortedSet<T> getSortedSet(String key) {
        RSortedSet<T> sortedSet = redissonClient.getSortedSet(key);
        return new TreeSet<>(sortedSet);
    }

    @Override
    public <T> RBloomFilter<T> createBloomFilter(String key, long expectedInsertions, double falsePositiveRate) {
        RBloomFilter<T> bloomFilter = redissonClient.getBloomFilter(key);
        bloomFilter.tryInit(expectedInsertions, falsePositiveRate);
        return bloomFilter;
    }
}
