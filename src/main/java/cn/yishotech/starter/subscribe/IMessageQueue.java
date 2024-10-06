/**
 * 项目名称:  redisson-plus-spring-boot-starter
 * 公司名称:  YiShoTech
 * All rights Reserved, Designed By YiShoTech 2023-2024
 */
package cn.yishotech.starter.subscribe;

import cn.yishotech.starter.model.Event;

import java.util.concurrent.TimeUnit;

/**
 * <p>类路径:cn.yishotech.starter.subscribe.IMessageQueue</p>
 * <p>类描述:消息队列接口</p>
 * <p>创建人:jason zong</p>
 * <p>创建时间:2024/10/06 17:26</p>
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
