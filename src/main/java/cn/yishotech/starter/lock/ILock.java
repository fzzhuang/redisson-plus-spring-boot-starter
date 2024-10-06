/**
 * 项目名称:  redisson-plus-spring-boot-starter
 * 公司名称:  YiShoTech
 * All rights Reserved, Designed By YiShoTech 2023-2024
 */
package cn.yishotech.starter.lock;

/**
 * <p>类路径:cn.yishotech.starter.lock.ILock</p>
 * <p>类描述:锁接口</p>
 * <p>创建人:jason zong</p>
 * <p>创建时间:2024/10/04 23:34</p>
 */
public interface ILock {

    /**
     * 获取锁
     *
     * @return 是否获取成功
     */
    boolean acquire();

    /**
     * 释放锁
     */
    void release();
}
