package cn.fuzhizhuang.starter.redisson.util;

import cn.fuzhizhuang.starter.redisson.distribute.DistributeCache;
import cn.fuzhizhuang.starter.redisson.model.DataType;
import cn.fuzhizhuang.starter.redisson.multi.MultiCache;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Method;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 缓存工具类
 *
 * @author Fu.zhizhuang
 */
public class CacheUtil {

    public static String parseKeys(Method method, Object[] args, String[] keys) {
        // key列表
        StringBuilder cacheKey = new StringBuilder();
        for (String key : keys) {
            // 解析el表达式
            String parsed = SpelUtil.parseEl(method, args, key);
            if (StringUtils.isBlank(parsed)) {
                return cacheKey.toString();
            }
            cacheKey.append("_").append(parsed);
        }
        return cacheKey.toString();
    }

    public static String buildCacheKey(String cachePrefix, String prefix, String key) {
        // 避免重复判断字符串是否为空
        boolean prefixBlank = StringUtils.isBlank(cachePrefix);
        String combinedPrefix = prefixBlank ? "" : cachePrefix + ":";
        return String.format("%s%s%s", combinedPrefix, prefix, key);
    }

    public static Object parsedValue(DataType type, Object value) {
        if (DataType.LIST.equals(type)) {
            List<?> list = (List<?>) value;
            if (!list.isEmpty()) return list;
        } else if (DataType.MAP.equals(type)) {
            Map<?, ?> map = (Map<?, ?>) value;
            if (!map.isEmpty()) return map;
        } else if (DataType.SET.equals(type)) {
            Set<?> set = (Set<?>) value;
            if (!set.isEmpty()) return set;
        } else if (DataType.SORTEDSET.equals(type)) {
            SortedSet<?> sortedSet = (SortedSet<?>) value;
            if (!sortedSet.isEmpty()) return sortedSet;
        } else {
            if (Objects.nonNull(value)) return value;
        }
        return null;
    }

    public static Object getCacheValue(Object value, DataType dataType) {
        if (DataType.LIST.equals(dataType)) {
            List<?> list = (List<?>) value;
            if (!list.isEmpty()) return new ArrayList<>(list);
        } else if (DataType.SET.equals(dataType)) {
            Set<?> set = (Set<?>) value;
            if (!set.isEmpty()) return set;
        } else if (DataType.SORTEDSET.equals(dataType)) {
            SortedSet<?> sortedSet = (SortedSet<?>) value;
            if (!sortedSet.isEmpty()) return sortedSet;
        } else if (DataType.MAP.equals(dataType)) {
            Map<?, ?> map = (Map<?, ?>) value;
            if (!map.isEmpty()) return map;
        } else if (DataType.DEFAULT.equals(dataType)) {
            if (Objects.nonNull(value)) return value;
        }
        return null;
    }

    /**
     * 设置缓存值
     *
     * @param cacheName  缓存名称
     * @param key        缓存key
     * @param value      缓存值
     * @param expire     过期时间
     * @param unit       时间单位
     * @param type       缓存类型
     * @param allowNull  缓存允许空值
     * @param multiCache 多级缓存
     * @return 缓存值
     */
    public static Object setMultiCacheValue(String cacheName, String key, Object value, long expire, TimeUnit unit, DataType type, boolean allowNull, MultiCache multiCache) {
        // 缓存允许空值
        if (allowNull || Objects.nonNull(value)) {
            multiCache.setValue(cacheName, key, value, expire, unit, type);
        }
        return value;
    }

    /**
     * 设置分布式缓存值
     *
     * @param key             缓存key
     * @param value           缓存值
     * @param type            缓存类型
     * @param expire          过期时间
     * @param unit            时间单位
     * @param distributeCache 分布式缓存
     */
    public static void setDistributedValue(String key, Object value, DataType type, long expire, TimeUnit unit, DistributeCache distributeCache) {
        boolean hasTimeout = expire > 0;
        switch (type) {
            case MAP:
                if (hasTimeout) {
                    distributeCache.setMap(key, (Map<?, ?>) value, expire, unit);
                } else {
                    distributeCache.setMap(key, (Map<?, ?>) value);
                }
                break;
            case SET:
                if (hasTimeout) {
                    distributeCache.setSet(key, (Set<?>) value, expire, unit);
                } else {
                    distributeCache.setSet(key, (Set<?>) value);
                }
                break;
            case LIST:
                if (hasTimeout) {
                    distributeCache.setList(key, (List<?>) value, expire, unit);
                } else {
                    distributeCache.setList(key, (List<?>) value);
                }
                break;
            case SORTEDSET:
                if (hasTimeout) {
                    distributeCache.setSortedSet(key, (SortedSet<?>) value, expire, unit);
                } else {
                    distributeCache.setSortedSet(key, (SortedSet<?>) value);
                }
                break;
            case DEFAULT:
                if (hasTimeout) {
                    distributeCache.setValue(key, value, expire, unit);
                } else {
                    distributeCache.setValue(key, value);
                }
                break;
        }
    }

    /**
     * 解析TimeUnit格式过期时间
     *
     * @param expire 过期时间
     * @param unit   单位
     * @return 过期时间
     */
    public static Duration parseTimeUnit(long expire, TimeUnit unit) {
        return switch (unit) {
            case MINUTES -> Duration.ofMinutes(expire);
            case HOURS -> Duration.ofHours(expire);
            case DAYS -> Duration.ofDays(expire);
            case MICROSECONDS -> Duration.ofMillis(expire);
            default -> Duration.ofSeconds(expire);
        };
    }
}
