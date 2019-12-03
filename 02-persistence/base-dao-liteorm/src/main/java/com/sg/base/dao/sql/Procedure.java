package com.sg.base.dao.sql;

/**
 * 存储过程操作接口。
 *
 * @author lpw
 */
public interface Procedure extends Jdbc {
    /**
     * 执行检索操作。
     *
     * @param sql  SQL。
     * @param args 参数集。
     * @return 数据集。
     */
    <T> T queryObject(String sql, Object[] args);
}
