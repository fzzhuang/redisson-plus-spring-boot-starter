package cn.fuzhizhuang.starter.redisson.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 更新缓存实体
 *
 * @author Fu.zhizhuang
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateCache {

    /**
     * L1缓存名称
     */
    private String cacheName;
    /**
     * 缓存key
     */
    private String key;
    /**
     * 缓存值
     */
    private Object value;
    /**
     * 操作类型
     */
    private OperateType operateType;
}
