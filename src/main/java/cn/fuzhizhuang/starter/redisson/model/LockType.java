package cn.fuzhizhuang.starter.redisson.model;

/**
 * 锁类型
 *
 * @author Fu.zhizhuang
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
