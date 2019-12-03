package com.sg.base.dao;

import com.alibaba.druid.pool.xa.DruidXADataSource;
import com.sg.base.bean.ContextRefreshedListener;
import com.sg.base.conf.DaoConfiguration;
import com.sg.base.dao.dialect.Dialect;
import com.sg.base.log.Logger;
import com.sg.base.util.Validator;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 数据源管理 只支持最多两个数据源，一个可读可写，一个只读 至于各自后的数据库的集群由集群模块进行管理
 *
 * @author Dai Wenqing
 * @date 2016/1/21
 */
@Repository("base.dao.datasource.manager")
public class DataSourceManager implements ContextRefreshedListener {
    @Autowired
    private DaoConfiguration daoConfiguration;
    private static Map<String, Map<Mode, DataSource>> dataSourceMap = new ConcurrentHashMap<>();
    private static Dialect dialect;
    private static String defaultDatasource = "##default";
    @Autowired
    private Set<Dialect> dialectSet;

    public Dialect getDialect() {
        return dialect;
    }

    public static String getDefaultDatasourceKey() {
        return defaultDatasource;
    }

    /*
     * 获取数据源
     *
     * @return
     */
    public static DataSource getDatasource(Mode mode, String... dataSource) {
        String key = "";
        if (dataSource.length > 0) {
            if (!Validator.isEmpty(dataSource[0])) {
                key = dataSource[0];
            }
        }
        if (Validator.isEmpty(key))
            key = defaultDatasource;
        if (mode == null)
            mode = Mode.Read;
        return dataSourceMap.get(key).get(mode);
    }

    public static Map<String, Map<Mode, DataSource>> getDatasourceMap() {
        return dataSourceMap;
    }

    /**
     * 获取所有数据源
     *
     * @return
     */
    /*
     * public List<DataSource> getDataSources() { List<DataSource>
     * dataSourceList = new ArrayList<>(); dataSourceMap.forEach((key, value) ->
     * { dataSourceList.add(value); }); return dataSourceList; }
     */
    @Override
    public int getContextRefreshedSort() {
        return 3;
    }

    @Override
    public void onContextRefreshed() {
        if (dialectSet == null || daoConfiguration == null)
            return;
        dialectSet.forEach(c -> {
            if (c.getName().equals(daoConfiguration.getDriver())) {
                dialect = c;
                return;
            }
        });

        createDataSource();
    }

    /**
     * 创建可能存在的多数据源
     */
    private void createDataSource() {
        if (daoConfiguration != null) {
            if (daoConfiguration.getServers() != null) {
                JSONArray jsonArray = JSONArray.fromObject(daoConfiguration.getServers());
                if (jsonArray != null) {
                    jsonArray.forEach(s -> {
                        JSONObject jsonObject = (JSONObject) s;
                        String key = jsonObject.getString("key");
                        Map<Mode, DataSource> datasource = new ConcurrentHashMap<>();
                        createSingleKeyDatasourceWithWR(datasource, jsonObject);
                        if (Validator.isEmpty(key)) {
                            key = defaultDatasource;
                        }
                        if (Logger.isDebugEnable())
                            Logger.debug("名称为[{}]的数据源[{}]初始化完毕", key, daoConfiguration.getServers());
                        dataSourceMap.put(key, datasource);
                    });
                }
            } else
                throw new NullPointerException("未配置服务器信息，snow.dao.database.servers值为空");
        } else
            throw new NullPointerException("无法获取到配置信息，请查看snow.conf.properties文件！");
    }

    /**
     * 创建单个关键字的数据源，包括其中的读与写的数据源
     *
     * @param dataSourceMap
     * @param jsonObject
     */
    private void createSingleKeyDatasourceWithWR(Map<Mode, DataSource> dataSourceMap, JSONObject jsonObject) {
        JSONArray jsonArray = jsonObject.getJSONArray("ips");
        if (jsonArray != null) {
            jsonArray.forEach(s -> {
                String[] serverProperties = s.toString().split(":");
                // BasicDataSource dataSource = new BasicDataSource();
                // DruidXADataSource DruidDataSource
                DruidXADataSource dataSource = new DruidXADataSource();
                Logger.info("使用的数据连接池组件是：{}", DruidXADataSource.class.getName());

                dataSource.setDriverClassName(dialect.getDriver());
                if (serverProperties == null || serverProperties.length < 2)
                    return;
                dataSource.setUrl(dialect.getUrl(serverProperties[0], jsonObject.getString("schema")));
                dataSource.setUsername(jsonObject.getString("u"));
                dataSource.setPassword(jsonObject.getString("p"));
                dataSource.setInitialSize(daoConfiguration.getInitialSize());
                dataSource.setMaxActive(daoConfiguration.getMaxActive());
                // dataSource.setMaxIdle(daoConfiguration.getMaxActive());
                dataSource.setMaxWait(daoConfiguration.getMaxWait());
                dataSource.setTestWhileIdle(daoConfiguration.getTestWhileIdle());
                dataSource.setTestOnBorrow(daoConfiguration.getTestOnBorrow());
                dataSource.setTestOnReturn(daoConfiguration.getTestOnReturn());
                dataSource.setValidationQuery(dialect.getValidationQuery());
                dataSource.setValidationQueryTimeout(daoConfiguration.getMaxWait());
                dataSource.setTimeBetweenEvictionRunsMillis(daoConfiguration.getTimeBetweenEvictionRunsMillis());
                // dataSource.setNumTestsPerEvictionRun(daoConfiguration.getMaxActive());
                dataSource.setRemoveAbandoned(daoConfiguration.getRemoveAbandoned());
                dataSource.setRemoveAbandonedTimeout(daoConfiguration.getRemoveAbandonedTimeout());
                dataSource.setLogAbandoned(daoConfiguration.getLogAbandoned());
                dataSource.setPoolPreparedStatements(daoConfiguration.getPoolPreparedStatements());
                dataSource.setMinEvictableIdleTimeMillis(daoConfiguration.getMinEvictableIdleTimeMillis());

                // druid 特有的属性 后期再考虑 重构
                try {
                    dataSource.setFilters(daoConfiguration.getFilters());
                } catch (SQLException ex) {
                }

                if (serverProperties[2].equals("0"))
                    dataSourceMap.put(Mode.Write, dataSource);
                else
                    dataSourceMap.put(Mode.Read, dataSource);
            });
            if (dataSourceMap.get(Mode.Read) == null)
                dataSourceMap.put(Mode.Read, dataSourceMap.get(Mode.Write));
        } else {
            throw new NullPointerException("未配置服务器信息，snow.dao.database.servers值中的ips属性为空");
        }
    }
}
