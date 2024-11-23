package cn.fuzhizhuang.starter.redisson.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 事件实体
 *
 * @author Fu.zhizhuang
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Event<T> {
    /**
     * 事件ID
     */
    private String id = UUID.randomUUID().toString();
    /**
     * 事件描述
     */
    private String desc;
    /**
     * 事件数据
     */
    private T data;
    /**
     * 发送时间
     */
    private LocalDateTime time = LocalDateTime.now();
}
