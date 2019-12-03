package com.sg.base.conf;

/**
 * HibernateConfiguration
 *
 * @author Dai Wenqing
 * @date 2016/1/23
 */
public interface HibernateConfiguration extends DaoConfiguration {
    /**
     * 是否输出SQL语句
     *
     * @return
     */
    default boolean showSql() {
        return false;
    }

    // use_second_level_cache

    /**
     * 是否使用二级缓存
     *
     * @return
     */
    default boolean useSecondLevelCache() {
        return false;
    }

    default String getPackagesToScan() {
        return "com.zoe.snow";
    }

}
