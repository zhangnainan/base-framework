package com.sg.base.dao;

import com.sg.base.bean.ContextRefreshedListener;
import com.sg.base.conf.DaoConfiguration;
import com.sg.base.dao.dialect.Dialect;
import com.sg.base.log.Logger;
import com.sg.base.util.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

/**
 * ConnectionManageImpl
 *
 * @author Dai Wenqing
 * @date 2016/1/23
 */
@Repository("base.dao.connection.manage")
public class ConnectionManageImpl extends ConnectionSupport<Connection> implements ContextRefreshedListener {
    private Dialect dialect;
    @Autowired(required = false)
    private Set<Dialect> dialectSet;
    @Autowired
    private DaoConfiguration daoConfiguration;

    @Override
    public Connection open(String datasource, Mode mode) {
        Connection c = null;
        try {
            // 获得的Session会在事务关闭或者回滚时,需要手动去关闭
            c = DataSourceManager.getDatasource(mode, datasource).getConnection();
            Logger.debug("成功建立connection，会话模式为：[{}]，数据源为：[{}]", mode, datasource);

            if (mode == Mode.Write) {
                if (transactional.get() != null) {
                    if (transactional.get())
                        // 开启事务，设置为不自动提交
                        c.setAutoCommit(false);
                }
            }
        } catch (Exception e) {
            Logger.error(e, "链接建立过程出现了异常！");
        }
        return c;
    }

    @Override
    public Connection fetch(List<Connection> caches, String datasource, Mode mode) {
        if (caches == null)
            return null;
        try {
            for (Connection c : caches) {
                if (!Validator.isEmpty(c) && !c.isClosed()) {
                    return c;
                }
            }
        } catch (Exception e) {
            Logger.error(e, "connection建立过程出现了异常！");
        }
        return null;
    }

    @Override
    public void rollback(Connection connection) {

        if (Validator.isEmpty(connection))
            return;

        try {
            if (!connection.isClosed()) {
                if (!connection.getAutoCommit()) {
                    connection.rollback();
                }
            }
        } catch (SQLException e) {
            Logger.warn(e, "回滚数据库连接时发生异常！");
        }


    }

    @Override
    public void commit(Connection connection) {

        if (Validator.isEmpty(connection))
            return;

        try {
            if (!connection.isClosed()) {
                if (!connection.getAutoCommit()) {
                    connection.commit();
                }
            }
        } catch (SQLException e) {
            Logger.warn(e, "关闭数据库连接时发生异常！");

            rollback();
        }


    }

    @Override
    public Dialect getDialect() {
        return dialect;
    }

    @Override
    public void close(Connection connection) {
        if (Validator.isEmpty(connection))
            return;

        try {
            if (!connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            Logger.warn(e, "关闭数据库连接时发生异常！");
        }

    }

    @Override
    public int getContextRefreshedSort() {
        return 9;
    }

    @Override
    public void onContextRefreshed() {
        if (dialectSet != null) {
            dialectSet.forEach(d -> {
                if (d.getName().equals(daoConfiguration.getDriver())) {
                    dialect = d;
                }
            });
        }
    }
}
