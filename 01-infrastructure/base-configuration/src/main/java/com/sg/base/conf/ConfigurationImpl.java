package com.sg.base.conf;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 统一配置实现类；
 * <p>
 * 1、独立部署singleton，不启动架构配置的加载容器的监听
 * <p>
 * 2、实现了默认的配置，可被WEB项目继承直接使用，与将所有配置放配在WEB项目中同一个道理，
 * 若有与默认配置不同的，可以将当前配置文件复制到当前WEB项目中，在其中修改配置值
 * <p>
 * 3、可独立部署，将配置放置分布式架构中zookeeper中， 做到统一配置，同步管理
 *
 * @author Dai Wenqing
 * @date 2016/1/19
 */
@Component("base.conf")
public class ConfigurationImpl implements Configuration, CrudConfiguration, CacheConfiguration, DaoConfiguration, HibernateConfiguration {
    @Value("${base.configuration.deploy}")
    private String deploy;

    @Value("${base.session.name}")
    private String sessionName;
    @Value("${base.cache.name}")
    private String cacheName;
    @Value("${base.dao.orm}")
    private String ormName;

    @Value("${base.dao.database.driver}")
    private String driver;
    @Value("${base.dao.database.servers}")
    private String servers;
    // @Value("${snow.dao.database.name}")
    private String databaseName;
    // @Value("${snow.dao.database.username}")
    private String databaseUserName;
    // @Value("${snow.dao.database.password}")
    private String databasePassword;
    @Value("${base.dao.database.initial-size}")
    private int initialSize = 0;
    @Value("${base.dao.database.max-active}")
    private int maxActive;
    @Value("${base.dao.database.max-wait}")
    private int maxWait;
    @Value("${base.dao.database.test-interval}")
    private int testInterval;
    @Value("${base.dao.database.remove-abandoned-timeout}")
    private int removeAbandonedTimeout;

    @Value("${base.dao.hibernate.use-second-level}")
    private String userSecondLevelCache;
    @Value("${base.dao.hibernate.show-sql}")
    private String showSql;
    @Value("${base.dao.hibernate.set-packages-to-scan}")
    private String packagesToScan;
    @Value("${base.datasource.name}")
    private String dataSourceName;

    @Value("${base.dao.remove-abandoned}")
    private boolean removeAbandoned;
    @Value("${base.dao.log-abandoned}")
    private boolean logAbandoned;
    @Value("${base.dao.time-between-eviction-runs-millis}")
    private int timeBetweenEvictionRunsMillis;
    @Value("${base.dao.min-evictable-idle-time-millis}")
    private int minEvictableIdleTimeMillis;
    @Value("${base.dao.test-while-idle}")
    private boolean testWhileIdle;
    @Value("${base.dao.test-on-borrow}")
    private boolean testOnBorrow;
    @Value("${base.dao.test-on-return}")
    private boolean testOnReturn;
    @Value("${base.dao.pool-prepared-statements}")
    private boolean poolPreparedStatements;
    @Value("${base.dao.maxPool-prepared-statement-per-connection-size}")
    private int maxPoolPreparedStatementPerConnectionSize;
    @Value("${base.dao.filters}")
    private String filters;


    @Override
    public String getCacheName() {
        return cacheName;
    }

    @Override
    public String getSessionName() {
        return sessionName;
    }

    @Override
    public String getServers() {
        return this.servers;
    }

    @Override
    public String getDriver() {
        return this.driver;
    }

    @Override
    public String getDatabaseUserName() {
        return this.databaseUserName;
    }

    @Override
    public String getDatabasePassword() {
        return this.databasePassword;
    }

    @Override
    public String getDatabaseName() {
        return this.databaseName;
    }

    @Override
    public int getInitialSize() {
        return this.initialSize;
    }

    @Override
    public int getMaxWait() {
        return this.maxWait;
    }

    @Override
    public int getMaxActive() {
        return this.maxActive;
    }

    @Override
    public int getTestInterval() {
        return this.testInterval;
    }

    @Override
    public int getRemoveAbandonedTimeout() {
        return this.removeAbandonedTimeout;
    }

    @Override
    public String getDataSourceName() {
        return null;
    }

    @Override
    public boolean showSql() {
        return this.showSql.toLowerCase().equals("true");
    }

    @Override
    public boolean useSecondLevelCache() {
        return this.userSecondLevelCache.toLowerCase().equals("true");
    }

    @Override
    public String getPackagesToScan() {
        return this.packagesToScan;
    }

    @Override
    public boolean getRemoveAbandoned() {
        return removeAbandoned;
    }

    @Override
    public boolean getLogAbandoned() {
        return logAbandoned;
    }

    @Override
    public int getTimeBetweenEvictionRunsMillis() {
        return timeBetweenEvictionRunsMillis;
    }

    @Override
    public int getMinEvictableIdleTimeMillis() {
        return minEvictableIdleTimeMillis;
    }

    @Override
    public boolean getTestWhileIdle() {
        return testWhileIdle;
    }

    @Override
    public boolean getTestOnBorrow() {
        return testOnBorrow;
    }

    @Override
    public boolean getTestOnReturn() {
        return testOnReturn;
    }

    @Override
    public boolean getPoolPreparedStatements() {
        return poolPreparedStatements;
    }

    @Override
    public int getMaxPoolPreparedStatementPerConnectionSize() {
        return maxPoolPreparedStatementPerConnectionSize;
    }

    @Override
    public String getFilters() {
        return filters;
    }

    @Override
    public String getOrmName() {
        return ormName;
    }


}
