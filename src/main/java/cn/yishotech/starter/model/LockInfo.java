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
 * <p>类路径:cn.yishotech.starter.model.LockInfo</p>
 * <p>类描述:锁信息</p>
 * <p>创建人:jason zong</p>
 * <p>创建时间:2024/10/04 23:28</p>
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
