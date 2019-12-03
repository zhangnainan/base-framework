package com.sg.base.dao.sql;

import net.sf.json.JSONArray;

/**
 * JDBC操作接口。
 *
 * @author lpw
 */
public interface Jdbc {
    /**
     * 执行检索操作。
     *
     * @param sql  SQL。
     * @param args 参数集。
     * @return 数据集。
     */
    SqlTable query(String sql, int size, int page, Object[] args);

    /**
     * 执行检索操作，并将结果集以JSON数组格式返回。
     *
     * @param sql  SQL。
     * @param args 参数集。
     * @return 数据集。
     */
    JSONArray queryAsJson(String sql, int size, int page, Object[] args);

    /**
     * 执行更新操作。
     *
     * @param sql  SQL。
     * @param args 参数集。
     * @return 影响记录数。
     */
    int update(String sql, Object[] args);

}
