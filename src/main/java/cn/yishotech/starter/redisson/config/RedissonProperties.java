/**
 * 项目名称:  redisson-plus-spring-boot-starter
 * 公司名称:  YiShoTech
 * All rights Reserved, Designed By YiShoTech 2023-2024
 */
package cn.yishotech.starter.redisson.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * <p>类路径:cn.yishotech.starter.config.RedissonProperties</p>
 * <p>类描述:Redisson配置属性</p>
 * <p>创建人:jason zong</p>
 * <p>创建时间:2024/10/05 19:52</p>
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "redisson")
public class RedissonProperties {

    /**
     * 是否开启
     */
    private boolean enabled = false;

    /**
     * Redisson 连接地址，例如：redis://127.0.0.1:6379。
     */
    private String address;

    /**
     * Redisson 连接密码，如果有密码设置则填写此项。
     */
    private String password;

    /**
     * 使用的数据库索引，默认为 0。
     */
    private int database = 0;

    /**
     * 连接超时时间，单位为毫秒，默认为 3000。
     */
    private int timeout = 3000;

    /**
     * 连接池大小，默认为 64。
     */
    private int connectionPoolSize = 64;

    /**
     * 最小空闲连接数，默认为 10。
     */
    private int idleConnectionSize = 10;

    /**
     * 连接超时时间，单位毫秒，默认为 10000。
     */
    private int connectTimeout = 10000;

    /**
     * 缓存前缀
     */
    private String cachePrefix;

    /**
     * 订阅发布频道名称前缀，默认为"redisson-pubsub-"。
     */
    private String pubSubChannelPrefix = "redisson-pubsub-";

    /**
     * 集群模式下节点扫描间隔时间，单位毫秒，默认为 1000。
     */
    private int scanInterval = 1000;

    /**
     * 从节点连接池大小，默认为 250。
     * 通常用于在 Redis Sentinel 或 Redis 集群环境下配置从节点的连接池大小。
     */
    private int slaveConnectionPoolSize = 250;

    /**
     * 主节点连接池大小，默认为 250。
     * 在 Redis Sentinel 或 Redis 集群环境下配置主节点的连接池大小。
     */
    private int masterConnectionPoolSize = 250;

    /**
     * Redis Sentinel 的节点地址列表。
     * 例如：["redis-sentinel1:26379", "redis-sentinel2:26379"]。
     * 用于连接到 Redis Sentinel 以实现高可用的 Redis 连接。
     */
    private String[] sentinelNodes;

    /**
     * 集群节点地址
     */
    private String[] clusterNodes;

    /**
     * 在 Redis Sentinel 环境下的主节点名称。
     * 通过此名称可以让 Redisson 自动识别主节点并进行连接切换。
     */
    private String masterName;

}
