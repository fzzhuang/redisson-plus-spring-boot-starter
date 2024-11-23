package cn.fuzhizhuang.starter.redisson.subscribe;

import cn.fuzhizhuang.starter.redisson.model.Event;

import java.util.concurrent.TimeUnit;

/**
 * 消息队列接口
 *
 * @author Fu.zhizhuang
 */
public interface IMessageQueue {

    /**
     * 发送消息
     *
     * @param topic 主题
     * @param event 事件
     */
    void sendMessage(String topic, Event<?> event);

    /**
     * 发送延迟消息
     *
     * @param topic 主题
     * @param event 事件
     * @param delay 延迟时长，默认分钟
     */
    void sendDelayMessage(String topic, Event<?> event, long delay);

    /**
     * 发送延迟消息
     *
     * @param topic    主题
     * @param event    事件
     * @param delay    延迟时长，默认分钟
     * @param timeUnit 单位
     */
    void sendDelayMessage(String topic, Event<?> event, long delay, TimeUnit timeUnit);

    /**
     * 取消延迟消息
     *
     * @param topic 主题
     * @param event 事件
     * @return 是否成功
     */
    boolean cancelDelayMessage(String topic, Event<?> event);
}
