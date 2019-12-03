package com.sg.base.dao;

import com.sg.base.dao.dialect.Dialect;
import com.sg.base.log.Logger;
import com.sg.base.util.Validator;

import javax.persistence.MappedSuperclass;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ConnectionSupport
 *
 * @author Dai Wenqing
 * @date 2016/8/26
 */
@MappedSuperclass()
public class ConnectionSupport<T> implements ConnectionManage<T> {
    protected ThreadLocal<Map<String, Map<Mode, List<T>>>> connections = new ThreadLocal<>();
    // ThreadLocal 只是对线程进行安全隔离，每个线程都需要重新创建对象
    protected ThreadLocal<Boolean> transactional = new ThreadLocal<>();
    protected ThreadLocal<Boolean> localTransactionalIsCommit = new ThreadLocal<>();

    @Override
    public boolean isCommit() {
        return localTransactionalIsCommit.get() == null ? false : localTransactionalIsCommit.get();
    }

    @Override
    public boolean hasBeginTransaction() {
        return transactional.get() == null ? false : transactional.get();
    }

    @Override
    public void beginTransaction() {
        transactional.set(true);
        localTransactionalIsCommit.set(false);
    }

    @Override
    public synchronized T get(Mode mode, String... datasource) {
        Logger.debug("开始建立会话........");
        T t = null;
        String key = DataSourceManager.getDefaultDatasourceKey();

        if (datasource.length > 0) {
            if (!Validator.isEmpty(datasource[0])) {
                key = datasource[0];
            }
        }
        Map<String, Map<Mode, List<T>>> openConnection = connections.get();
        Map<Mode, List<T>> modeTMap = null;
        List<T> tList = null;
        if (Validator.isEmpty(openConnection)) {
            openConnection = new HashMap<>();
            connections.set(openConnection);
        } else
            modeTMap = openConnection.get(key);
        if (Validator.isEmpty(modeTMap)) {
            modeTMap = new HashMap<>();
            openConnection.put(key, modeTMap);
        } else
            tList = modeTMap.get(mode);
        if (Validator.isEmpty(tList)) {
            tList = new ArrayList<T>();
            modeTMap.put(mode, tList);
        }

        t = fetch(tList, key, mode);
        if (t == null) {
            T openT = open(key, mode);
            t = openT;
            tList.add(t);
        }

        return t;
    }

    @Override
    public T open(String datasource, Mode mode) {
        return null;
    }

    @Override
    public T fetch(List<T> caches, String datasource, Mode mode) {
        return null;
    }

    @Override
    public void rollback() {
        if (connections.get() != null) {
            connections.get().forEach((key, modeSessionMap) -> {
                try {
                    if (!Validator.isEmpty(modeSessionMap)) {
                        modeSessionMap.forEach((k, v) -> {
                            if (!Validator.isEmpty(v))
                                v.forEach(c -> rollback(c));
                        });
                    }

                } catch (Exception ex) {
                    Logger.error(ex, "事务回滚失败！");
                }
                if (Logger.isDebugEnable())
                    Logger.debug("进行了事务回滚！");
            });
        }
    }

    @Override
    public void rollback(T connection) {
    }

    @Override
    public void commit() {
        if (connections.get() != null) {
            connections.get().forEach((key, modeSessionMap) -> {
                try {
                    if (!Validator.isEmpty(modeSessionMap)) {
                        modeSessionMap.forEach((k, connection) -> {
                            if (!Validator.isEmpty(connection))
                                connection.forEach(c -> commit(c));
                        });
                    }
                } catch (Exception ex) {
                    Logger.error(ex, "事务提交失败！");
                    rollback();
                }

            });
            localTransactionalIsCommit.set(true);
        }
    }

    @Override
    public void commit(T connection) {
    }

    @Override
    public void close(T connection) {
    }

    @Override
    public void close() {
        //有开启事务都没有提交，不能关闭会话
        if (transactional.get() != null)
            if (transactional.get() && !isCommit())
                return;
        if (connections.get() != null) {
            connections.get().forEach((key, modeSessionMap) -> {
                try {
                    if (!Validator.isEmpty(modeSessionMap)) {
                        modeSessionMap.forEach((k, connection) -> {
                            if (!Validator.isEmpty(connection))
                                connection.forEach(c -> close(c));
                        });
                    }

                } catch (Exception ex) {
                    Logger.error(ex, ex.getMessage());
                }
                Logger.debug("hibernate关闭了数据库链接会话,数据源为[]", key);
            });
            connections.get().clear();
        }
        transactional.remove();
        connections.remove();

    }

    @Override
    public Dialect getDialect() {
        return null;
    }
}
