package cn.fzzhuang.starter.redisson.event;

import cn.fzzhuang.starter.redisson.model.Event;
import cn.fzzhuang.starter.redisson.model.UpdateCache;
import lombok.Getter;
import lombok.Setter;

/**
 * 更新L1缓存事件
 *
 * @author Fu.zhizhuang
 */
@Setter
@Getter
public class UpdateL1CacheEvent extends Event<UpdateCache> {

    public static UpdateL1CacheEvent create(UpdateCache event) {
        UpdateL1CacheEvent updateL1CacheEvent = new UpdateL1CacheEvent();
        updateL1CacheEvent.setDesc("updateL1Cache");
        updateL1CacheEvent.setData(event);
        return updateL1CacheEvent;
    }
}
