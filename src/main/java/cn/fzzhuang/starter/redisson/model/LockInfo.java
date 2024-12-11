package cn.fzzhuang.starter.redisson.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 锁信息
 *
 * @author Fu.zhizhuang
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LockInfo {

    /*锁名称*/
    private String name;

    /*锁类型*/
    private LockType lockType;

    /*尝试加锁，最长等待时间*/
    private long waitTime;

    /*上锁以后，自动解锁时间 */
    private long leaseTime;
}
