package cn.fuzhizhuang.starter.redisson.event;

import cn.fuzhizhuang.starter.redisson.model.Event;
import cn.fuzhizhuang.starter.redisson.model.UpdateCache;
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
        updateL1CacheEvent.setDesc("更新L1缓存");
        updateL1CacheEvent.setData(event);
        return updateL1CacheEvent;
    }
}
