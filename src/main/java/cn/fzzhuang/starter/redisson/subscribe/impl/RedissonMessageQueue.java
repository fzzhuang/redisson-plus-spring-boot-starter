package cn.fzzhuang.starter.redisson.subscribe.impl;

import cn.fzzhuang.starter.redisson.model.Event;
import cn.fzzhuang.starter.redisson.subscribe.IMessageQueue;
import com.alibaba.fastjson2.JSON;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBlockingQueue;
import org.redisson.api.RDelayedQueue;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Redisson实现消息队列
 *
 * @author Fu.zhizhuang
 */
@Slf4j
@Component
public class RedissonMessageQueue implements IMessageQueue {

    @Resource
    private RedissonClient redissonClient;

    @Override
    public void sendMessage(String topic, Event<?> event) {
        log.debug("发送消息 topic:{} event:{}", topic, JSON.toJSONString(event));
        RTopic rTopic = redissonClient.getTopic(topic);
        rTopic.publish(event);
    }

    @Override
    public void sendDelayMessage(String topic, Event<?> event, long delay) {
        log.debug("延迟队列 topic:{} event:{} delay:{}", topic, JSON.toJSONString(event), delay);
        // 1. 创建阻塞队列
        RBlockingQueue<Object> blockingQueue = redissonClient.getBlockingQueue(topic);
        // 2. 将创建的阻塞队列放入延迟队列中
        RDelayedQueue<Object> delayedQueue = redissonClient.getDelayedQueue(blockingQueue);
        // 3. 发送消息到延迟队列中
        delayedQueue.offer(event, delay, TimeUnit.MINUTES);
    }

    @Override
    public void sendDelayMessage(String topic, Event<?> event, long delay, TimeUnit timeUnit) {
        log.debug("延迟队列 topic:{} event:{} delay:{} unit:{}", topic, JSON.toJSONString(event), delay, timeUnit);
        // 1. 创建阻塞队列
        RBlockingQueue<Object> blockingQueue = redissonClient.getBlockingQueue(topic);
        // 2. 将创建的阻塞队列放入延迟队列中
        RDelayedQueue<Object> delayedQueue = redissonClient.getDelayedQueue(blockingQueue);
        // 3. 发送消息到延迟队列中
        delayedQueue.offer(event, delay, timeUnit);
    }

    @Override
    public boolean cancelDelayMessage(String topic, Event<?> event) {
        log.debug("移除延迟队列 topic:{} event:{}", topic, JSON.toJSONString(event));
        return redissonClient.getBlockingDeque(topic).remove(event);
    }
}
