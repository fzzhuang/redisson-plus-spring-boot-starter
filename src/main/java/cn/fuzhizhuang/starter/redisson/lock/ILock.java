package cn.fuzhizhuang.starter.redisson.lock;

/**
 * 锁接口
 *
 * @author Fu.zhizhuang
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
