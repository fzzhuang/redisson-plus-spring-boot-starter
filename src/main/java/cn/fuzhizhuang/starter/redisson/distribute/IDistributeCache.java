package cn.fuzhizhuang.starter.redisson.distribute;

import org.redisson.api.RBloomFilter;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.concurrent.TimeUnit;

/**
 * 分布式缓存接口
 *
 * @author Fu.zhizhuang
 */
public interface IDistributeCache {

    /**
     * 向缓存中保存数据
     *
     * @param key   键
     * @param value 值
     */
    void setValue(String key, Object value);

    /**
     * 向缓存中保存数据，过期时间（单位：秒）
     *
     * @param key    键
     * @param value  值
     * @param expire 过期时间，单位：秒
     */
    void setValue(String key, Object value, long expire);

    /**
     * 向缓存中保存数据
     *
     * @param key      键
     * @param value    值
     * @param expire   过期时间
     * @param timeUnit 时间单位
     */
    void setValue(String key, Object value, long expire, TimeUnit timeUnit);

    /**
     * 获取数据
     *
     * @param key 键
     * @param <T> 泛型
     * @return 数据
     */
    <T> T getValue(String key);

    /**
     * 自增
     *
     * @param key 键
     * @return 自增后的值
     */
    Long incrementValue(String key);

    /**
     * 自增给定的delta
     *
     * @param key   键
     * @param delta delta
     * @return 自增后的值
     */
    Long incrementValue(String key, long delta);

    /**
     * 自减
     *
     * @param key 键
     * @return 自减后的值
     */
    Long decrementValue(String key);

    /**
     * 自减给定的delta
     *
     * @param key   键
     * @param delta delta
     * @return 自减后的值
     */
    Long decrementValue(String key, long delta);

    /**
     * 删除指定键的值
     *
     * @param key 键
     */
    void removeValue(String key);

    /**
     * 缓存map类型数据，并设置缓存时间，单位分钟
     *
     * @param key    键
     * @param map    map
     * @param expire 过期时间
     * @param <K>    键泛型
     * @param <V>    值泛型
     */
    <K, V> void setMap(String key, Map<K, V> map, long expire);

    /**
     * 缓存map类型数据，并设置缓存时间，单位分钟
     *
     * @param key      键
     * @param map      map
     * @param expire   过期时间
     * @param timeUnit 时间单位
     * @param <K>      键泛型
     * @param <V>      值泛型
     */
    <K, V> void setMap(String key, Map<K, V> map, long expire, TimeUnit timeUnit);

    /**
     * 缓存map类型数据，并设置缓存时间，单位分钟
     *
     * @param key 键
     * @param map map
     * @param <K> 键泛型
     * @param <V> 值泛型
     */
    <K, V> void setMap(String key, Map<K, V> map);

    /**
     * 获取缓存Map
     *
     * @param key keys
     * @param <K> 键泛型
     * @param <V> 值泛型
     * @return map信息
     */
    <K, V> Map<K, V> getMap(String key);

    /**
     * 获取缓存值
     *
     * @param key    键
     * @param mapKey map 键
     * @param <K>    键泛型
     * @param <V>    值泛型
     * @return 值
     */
    <K, V> V getMapValue(String key, K mapKey);

    /**
     * 缓存列表数据
     *
     * @param key  键
     * @param list 列表信息
     * @param <T>  泛型
     */
    <T> void setList(String key, List<T> list);

    /**
     * 缓存列表数据
     *
     * @param key    键
     * @param list   列表信息
     * @param expire 超时时间
     * @param <T>    泛型
     */
    <T> void setList(String key, List<T> list, long expire);

    /**
     * 缓存列表数据
     *
     * @param key      键
     * @param list     列表信息
     * @param expire   超时时间
     * @param timeUnit 单位
     * @param <T>      泛型
     */
    <T> void setList(String key, List<T> list, long expire, TimeUnit timeUnit);

    /**
     * 获取列表信息
     *
     * @param key 键
     * @param <T> 泛型
     * @return 列表信息
     */
    <T> List<T> getList(String key);

    /**
     * 缓存集合信息
     *
     * @param key 键
     * @param set 集合
     * @param <T> 泛型
     */
    <T> void setSet(String key, Set<T> set);

    /**
     * 缓存集合信息
     *
     * @param key    键
     * @param set    集合
     * @param expire 超时时间
     * @param <T>    泛型
     */
    <T> void setSet(String key, Set<T> set, long expire);

    /**
     * 缓存集合信息
     *
     * @param key      键
     * @param set      集合
     * @param expire   超时时间
     * @param timeUnit 单位
     * @param <T>      泛型
     */
    <T> void setSet(String key, Set<T> set, long expire, TimeUnit timeUnit);

    /**
     * 获取集合信息
     *
     * @param key 键
     * @param <T> 泛型
     * @return 集合信息
     */
    <T> Set<T> getSet(String key);

    /**
     * 缓存有序集合
     *
     * @param key       键
     * @param sortedSet 有序集合
     * @param <T>       泛型
     */
    <T> void setSortedSet(String key, SortedSet<T> sortedSet);

    /**
     * 缓存有序集合
     *
     * @param key       键
     * @param sortedSet 有序集合
     * @param expire    超时时间
     * @param <T>       泛型
     */
    <T> void setSortedSet(String key, SortedSet<T> sortedSet, long expire);

    /**
     * 缓存有序集合
     *
     * @param key       键
     * @param sortedSet 有序集合
     * @param expire    超时时间
     * @param timeUnit  单位
     * @param <T>       泛型
     */
    <T> void setSortedSet(String key, SortedSet<T> sortedSet, long expire, TimeUnit timeUnit);

    /**
     * 获取有序缓存
     *
     * @param key 键
     * @param <T> 泛型
     * @return 有序缓存
     */
    <T> SortedSet<T> getSortedSet(String key);

    /**
     * 创建布隆过滤器
     *
     * @param key                键
     * @param expectedInsertions 预期插入的数据量
     * @param falsePositiveRate  期望误差率
     * @param <T>                泛型
     * @return 布隆过滤器
     */
    <T> RBloomFilter<T> createBloomFilter(String key, long expectedInsertions, double falsePositiveRate);
}
