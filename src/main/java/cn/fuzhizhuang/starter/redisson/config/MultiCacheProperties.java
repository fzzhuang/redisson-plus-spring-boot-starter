package cn.fuzhizhuang.starter.redisson.config;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 多级缓存配置属性
 *
 * @author Fu.zhizhuang
 */
@Setter
@Getter
@ConfigurationProperties(prefix = "multi.cache")
public class MultiCacheProperties {

    /**
     * 是否允许缓存null值
     */
    private boolean allowNullValue = false;

    private List<L1Cache> l1Caches;

    @Data
    public static class L1Cache {
        private String cacheName;
        private int expire = 0;
        private int initialCapacity = 128;
        private int maximumSize = 1024;
        private TimeUnit timeUnit = TimeUnit.SECONDS;
    }

}
