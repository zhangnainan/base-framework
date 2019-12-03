package com.sg.base.dao;

import com.sg.base.dao.dialect.Dialect;

import java.util.List;

/**
 * DAO连接。 定义DAO连接接口。
 *
 * @author lpw
 */
public interface ConnectionManage<T> extends Closable, Transaction {

    /**
     * 获取一个数据连接。返回线程安全的数据连接，即每个线程使用独立的数据连接。
     *
     * @param mode       数据操作方式 ；为null则获取可读写数据源。
     * @param datasource 指定数据源
     * @return 数据连接；如果获取失败则返回null。
     */
    T get(Mode mode, String... datasource);

    /**
     * 打开会话
     *
     * @param datasource
     * @param mode
     * @return
     */
    T open(String datasource, Mode mode);

    T fetch(List<T> caches, String datasource, Mode mode);

    /**
     * 手动回滚本次事务所有更新操作。
     */
    void rollback();

    void rollback(T connection);

    /**
     * 提交持久化，并关闭当前线程的所有连接。
     */
    void commit();

    void commit(T connection);

    void close(T connection);

    /**
     * 获取本地化
     */
    Dialect getDialect();
}
