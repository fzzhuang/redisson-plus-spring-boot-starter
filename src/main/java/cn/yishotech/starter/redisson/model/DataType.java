/**
 * 项目名称:  redisson-plus-spring-boot-starter
 * 公司名称:  YiShoTech
 * All rights Reserved, Designed By YiShoTech 2023-2024
 */
package cn.yishotech.starter.redisson.model;

/**
 * <p>类路径:cn.yishotech.starter.model.DataType</p>
 * <p>类描述:缓存数据类型枚举</p>
 * <p>创建人:jason zong</p>
 * <p>创建时间:2024/10/05 19:32</p>
 */
public enum DataType {

    /* 默认 */
    DEFAULT,

    /* 列表 */
    LIST,

    /* 哈希 */
    MAP,

    /* 集合 */
    SET,

    /* 有序集合 */
    SORTEDSET,
}
