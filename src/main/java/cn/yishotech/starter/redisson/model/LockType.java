/**
 * 项目名称:  redisson-plus-spring-boot-starter
 * 公司名称:  YiShoTech
 * All rights Reserved, Designed By YiShoTech 2023-2024
 */
package cn.yishotech.starter.redisson.model;

/**
 * <p>类路径:cn.yishotech.starter.model.LockType</p>
 * <p>类描述:锁类型</p>
 * <p>创建人:jason zong</p>
 * <p>创建时间:2024/10/04 23:28</p>
 */
public enum LockType {

    /*可重入锁*/
    Reentrant,

    /*公平锁*/
    Fair,

    /*读锁*/
    Read,

    /*写锁*/
    Write,
}
