/**
 * 项目名称:  redisson-plus-spring-boot-starter
 * 公司名称:  YiShoTech
 * All rights Reserved, Designed By YiShoTech 2023-2024
 */
package cn.yishotech.starter.config;

import cn.yishotech.starter.annotation.RedissonListener;
import cn.yishotech.starter.aop.*;
import cn.yishotech.starter.distribute.impl.RedissonDistributeCache;
import cn.yishotech.starter.lock.factory.LockFactory;
import cn.yishotech.starter.lock.impl.RedissonDistributedLock;
import cn.yishotech.starter.multi.impl.MultiCache;
import cn.yishotech.starter.multi.listener.UpdateL1CacheListener;
import cn.yishotech.starter.subscribe.impl.RedissonMessageQueue;
import com.alibaba.fastjson2.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.Redisson;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.redisson.api.listener.MessageListener;
import org.redisson.client.codec.Codec;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.ClusterServersConfig;
import org.redisson.config.Config;
import org.redisson.config.SentinelServersConfig;
import org.redisson.config.SingleServerConfig;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * <p>类路径:cn.yishotech.starter.config.RedissonPlusAutoConfiguration</p>
 * <p>类描述:Redisson Plus自动配置</p>
 * <p>创建人:jason zong</p>
 * <p>创建时间:2024/10/06 14:40</p>
 */
@Slf4j
@Configuration
@ConditionalOnClass(Config.class)
@EnableConfigurationProperties({RedissonProperties.class, MultiCacheProperties.class})
public class RedissonPlusAutoConfiguration {


    @Bean(destroyMethod = "shutdown")
    public RedissonClient redissonClient(ConfigurableApplicationContext applicationContext, RedissonProperties redissonProperties) {
        Config config = new Config();
        // 配置 Jackson 的 ObjectMapper 以支持 LocalDateTime 的序列化和反序列化
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        Codec codec = new JsonJacksonCodec(objectMapper);
        config.setCodec(codec);
        String[] sentinelNodes = redissonProperties.getSentinelNodes();
        String[] clusterNodes = redissonProperties.getClusterNodes();
        if (Objects.nonNull(sentinelNodes) && sentinelNodes.length > 0) {
            // 配置哨兵模式
            String masterName = redissonProperties.getMasterName();
            String[] sentinelAddresses = redissonProperties.getSentinelNodes();
            SentinelServersConfig sentinelServersConfig = config.useSentinelServers().addSentinelAddress(sentinelAddresses)
                    .setTimeout(redissonProperties.getTimeout())
                    .setScanInterval(redissonProperties.getScanInterval())
                    .setSlaveConnectionPoolSize(redissonProperties.getSlaveConnectionPoolSize())
                    .setMasterName(masterName);
            log.info("配置哨兵模式：{}", JSON.toJSONString(sentinelServersConfig));
        } else if (Objects.nonNull(clusterNodes) && clusterNodes.length > 0) {
            // 配置集群模式
            ClusterServersConfig clusterServersConfig = config.useClusterServers().addNodeAddress(redissonProperties.getClusterNodes())
                    .setTimeout(redissonProperties.getTimeout())
                    .setScanInterval(redissonProperties.getScanInterval())
                    .setMasterConnectionPoolSize(redissonProperties.getMasterConnectionPoolSize())
                    .setSlaveConnectionPoolSize(redissonProperties.getSlaveConnectionPoolSize());
            if (StringUtils.isNotBlank(redissonProperties.getPassword())) {
                clusterServersConfig.setPassword(redissonProperties.getPassword());
            }
            log.info("配置集群模式：{}", JSON.toJSONString(clusterServersConfig));
        } else {
            // 单节点模式配置
            String address = redissonProperties.getAddress();
            address = address.startsWith("redis://") ? address : "redis://" + address;
            SingleServerConfig singleServerConfig = config.useSingleServer().setAddress(address)
                    .setTimeout(redissonProperties.getTimeout())
                    .setDatabase(redissonProperties.getDatabase())
                    .setConnectionPoolSize(redissonProperties.getConnectionPoolSize())
                    .setConnectionMinimumIdleSize(redissonProperties.getIdleConnectionSize())
                    .setConnectTimeout(redissonProperties.getConnectTimeout());
            if (StringUtils.isNotBlank(redissonProperties.getPassword())) {
                singleServerConfig.setPassword(redissonProperties.getPassword());
            }
            log.info("配置单机模式:{}", JSON.toJSONString(singleServerConfig));
        }
        RedissonClient redissonClient = Redisson.create(config);

        // 注册消息发布订阅主题Topic
        // 找到所有实现了Redisson中MessageListener接口的bean名字
        String[] beanNamesForType = applicationContext.getBeanNamesForType(MessageListener.class);
        for (String beanName : beanNamesForType) {
            // 通过bean名字获取到监听bean
            MessageListener<?> bean = applicationContext.getBean(beanName, MessageListener.class);
            Class<? extends MessageListener> beanClass = bean.getClass();
            // 如果bean的注解里包含我们的自定义注解RedissonListener.class，则以RedissonListener注解的值作为name将该bean注册到bean工厂，方便在别处注入
            if (beanClass.isAnnotationPresent(RedissonListener.class)) {
                RedissonListener redissonListener = beanClass.getAnnotation(RedissonListener.class);
                RTopic topic = redissonClient.getTopic(redissonListener.topic());
                topic.addListener(Object.class, bean);
                ConfigurableListableBeanFactory beanFactory = applicationContext.getBeanFactory();
                beanFactory.registerSingleton(redissonListener.topic(), topic);
            }
        }
        return redissonClient;
    }

    @Bean
    public LockAspect lockAspect() {
        return new LockAspect();
    }

    @Bean
    public RedissonCacheAspect redissonCacheAspect() {
        return new RedissonCacheAspect();
    }

    @Bean
    public RedissonCacheEvictAspect redissonCacheEvictAspect() {
        return new RedissonCacheEvictAspect();
    }

    @Bean
    public RedissonCachePutAspect redissonCachePutAspect() {
        return new RedissonCachePutAspect();
    }

    @Bean
    public RedissonDistributeCache redissonDistributeCache() {
        return new RedissonDistributeCache();
    }

    @Bean
    public RedissonDistributedLock redissonDistributedLock() {
        return new RedissonDistributedLock();
    }

    @Bean
    public LockFactory lockFactory() {
        return new LockFactory();
    }

    @Bean
    public RedissonMessageQueue redissonMessageQueue() {
        return new RedissonMessageQueue();
    }

    @Bean("localCacheMap")
    public Map<String, Cache<String, Object>> localCacheMap(MultiCacheProperties properties) {
        List<MultiCacheProperties.L1Cache> l1Caches = properties.getL1Caches();
        Map<String, Cache<String, Object>> cacheMap = new ConcurrentHashMap<>();
        for (MultiCacheProperties.L1Cache l1Cache : l1Caches) {
            Cache<String, Object> cache = Caffeine.newBuilder()
                    .initialCapacity(l1Cache.getInitialCapacity())
                    .maximumSize(l1Cache.getMaximumSize())
                    .expireAfterWrite(parseTimeUnit(l1Cache.getExpire(), l1Cache.getTimeUnit()))
                    .build();
            cacheMap.put(l1Cache.getCacheName(), cache);
        }
        return cacheMap;
    }

    @Bean
    public MultiCache multiCache() {
        return new MultiCache();
    }

    @Bean
    public MultiCacheAspect multiCacheAspect() {
        return new MultiCacheAspect();
    }

    @Bean
    public MultiCacheEvictAspect multiCacheEvictAspect() {
        return new MultiCacheEvictAspect();
    }

    @Bean
    public MultiCachePutAspect multiCachePutAspect() {
        return new MultiCachePutAspect();
    }

    @Bean
    public UpdateL1CacheListener updateL1CacheListener() {
        return new UpdateL1CacheListener();
    }

    /**
     * 解析TimeUnit格式过期时间
     *
     * @param expire 过期时间
     * @param unit   单位
     * @return 过期时间
     */
    private Duration parseTimeUnit(long expire, TimeUnit unit) {
        return switch (unit) {
            case MINUTES -> Duration.ofMinutes(expire);
            case HOURS -> Duration.ofHours(expire);
            case DAYS -> Duration.ofDays(expire);
            case MICROSECONDS -> Duration.ofMillis(expire);
            default -> Duration.ofSeconds(expire);
        };
    }
}
