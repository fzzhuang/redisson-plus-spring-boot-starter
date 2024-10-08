/**
 * 项目名称:  redisson-plus-spring-boot-starter
 * 公司名称:  YiShoTech
 * All rights Reserved, Designed By YiShoTech 2023-2024
 */
package cn.yishotech.starter.redisson.config;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * <p>类路径:cn.yishotech.starter.config.MultiCacheProperties</p>
 * <p>类描述:多级缓存配置属性</p>
 * <p>创建人:jason zong</p>
 * <p>创建时间:2024/10/06 19:50</p>
 */
@Setter
@Getter
@ConfigurationProperties(prefix = "multi.cache")
public class MultiCacheProperties {

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
