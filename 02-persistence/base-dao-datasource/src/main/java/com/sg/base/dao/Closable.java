package com.sg.base.dao;

/**
 * 可关闭持久化接口定义。
 *
 * @author lpw
 */
public interface Closable {
    /**
     * 提交持久化，并关闭当前线程的所有连接。
     */
    void close();
}
