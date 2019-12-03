package com.sg.base.conf;

/**
 * DaoConfiguration
 *
 * @author Dai Wenqing
 * @date 2016/1/21
 */
public interface DaoConfiguration {
    /**
     * 获取服务器列表
     *
     * @return
     */
    String getServers();

    /**
     * 获取数据库驱动
     *
     * @return
     */
    String getDriver();

    /**
     * 获取用户名
     *
     * @return
     */
    String getDatabaseUserName();

    /**
     * 获取密码
     *
     * @return
     */
    String getDatabasePassword();

    /**
     * 获取数据库名
     *
     * @return
     */
    String getDatabaseName();

    /**
     * 连接池最大激活数量，如果非正整数，则不做限制。。
     *
     * @return
     */
    int getInitialSize();

    /**
     * 连接池连接最大等待时间（单位毫秒）， -1 则将无限等待
     *
     * @return
     */
    int getMaxWait();

    /**
     * 连接池最大激活数量，如果非正整数，则不做限制。
     *
     * @return
     */
    int getMaxActive();

    /**
     * 连接检测间隔时间（单位：毫秒），0表示不检测。
     *
     * @return
     */
    int getTestInterval();

    /**
     * 设置自动断开连接最大时长，单位：秒。
     *
     * @return
     */
    int getRemoveAbandonedTimeout();

    String getDataSourceName();

    boolean getRemoveAbandoned();

    boolean getLogAbandoned();

    int getTimeBetweenEvictionRunsMillis();

    int getMinEvictableIdleTimeMillis();

    boolean getTestWhileIdle();

    boolean getTestOnBorrow();

    boolean getTestOnReturn();

    boolean getPoolPreparedStatements();

    int getMaxPoolPreparedStatementPerConnectionSize();

    String getFilters();
}
