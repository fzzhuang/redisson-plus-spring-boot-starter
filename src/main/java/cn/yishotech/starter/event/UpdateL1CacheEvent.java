/**
 * 项目名称:  redisson-plus-spring-boot-starter
 * 公司名称:  YiShoTech
 * All rights Reserved, Designed By YiShoTech 2023-2024
 */
package cn.yishotech.starter.event;

import cn.yishotech.starter.model.Event;
import cn.yishotech.starter.model.UpdateCache;
import lombok.*;

/**
 * <p>类路径:cn.yishotech.starter.event.UpdateL1CacheEvent</p>
 * <p>类描述:更新L1缓存事件</p>
 * <p>创建人:jason zong</p>
 * <p>创建时间:2024/10/07 17:45</p>
 */
@Setter
@Getter
public class UpdateL1CacheEvent extends Event<UpdateCache> {

    public static UpdateL1CacheEvent create(UpdateCache event) {
        UpdateL1CacheEvent updateL1CacheEvent = new UpdateL1CacheEvent();
        updateL1CacheEvent.setDesc("更新L1缓存");
        updateL1CacheEvent.setData(event);
        return updateL1CacheEvent;
    }
}
