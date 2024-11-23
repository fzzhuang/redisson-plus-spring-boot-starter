package cn.fuzhizhuang.starter.redisson.multi;

import cn.fuzhizhuang.starter.redisson.model.DataType;
import cn.fuzhizhuang.starter.redisson.model.OperateType;

import java.util.concurrent.TimeUnit;

/**
 * 多级缓存接口
 *
 * @author Fu.zhizhuang
 */
public interface IMultiCache {

    /**
     * 存储缓存
     *
     * @param cacheName 一级缓存名
     * @param key       键
     * @param value     值
     * @param dataType  数据类型
     */
    void setValue(String cacheName, String key, Object value, DataType dataType);

    /**
     * 存储缓存
     *
     * @param cacheName 一级缓存名
     * @param key       键
     * @param value     值
     * @param timeout   超时时间, 单位：分钟
     * @param dataType  数据类型
     */
    void setValue(String cacheName, String key, Object value, long timeout, DataType dataType);

    /**
     * 存储缓存
     *
     * @param cacheName 一级缓存名
     * @param key       键
     * @param value     值
     * @param timeout   超时时间
     * @param unit      时间单位
     * @param dataType  数据类型
     */
    void setValue(String cacheName, String key, Object value, long timeout, TimeUnit unit, DataType dataType);

    /**
     * 获取缓存值
     *
     * @param cacheName 一级缓存名
     * @param key       键
     * @param dataType  数据类型
     * @return 缓存信息
     */
    <T> T getValue(String cacheName, String key, DataType dataType);

    /**
     * 删除缓存
     *
     * @param cacheName 一级缓存名
     * @param key       键
     */
    void removeValue(String cacheName, String key);

    /**
     * 更新一级缓存
     *
     * @param cacheName   缓存名
     * @param key         键
     * @param value       值
     * @param operateType 操作类型
     */
    void updateL1Cache(String cacheName, String key, Object value, OperateType operateType);
}
