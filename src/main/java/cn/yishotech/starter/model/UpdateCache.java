/**
 * 项目名称:  redisson-plus-spring-boot-starter
 * 公司名称:  YiShoTech
 * All rights Reserved, Designed By YiShoTech 2023-2024
 */
package cn.yishotech.starter.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>类路径:cn.yishotech.starter.model.UpdateCache</p>
 * <p>类描述:更新缓存实体</p>
 * <p>创建人:jason zong</p>
 * <p>创建时间:2024/10/06 23:00</p>
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
