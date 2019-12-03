package com.sg.base.dao.sql;

import com.sg.base.model.Model;
import com.sg.base.model.PageList;

import java.util.List;

/**
 * SQL操作接口。
 *
 * @author lpw
 */
public interface Sql extends Jdbc {

    /**
     * 执行批量更新操作。
     *
     * @param sql  SQL。
     * @param args 参数集。
     * @return 影响记录数。
     */
    int[] update(String sql, List<Object[]> args);

    /**
     * 执行SQL
     *
     * @param sql  sql语句
     * @param args 参数
     * @return
     */
    Boolean execute(String sql, Object[] args);

    /**
     * 获取对象分页数据列表
     *
     * @param classZ 要返回的对象类型
     * @param sql    SQL语句
     * @param page   当然页数,想获取全部的数据将page值设置成小1
     * @param size   每页多少条
     * @param <T>
     * @return
     */
    <T extends Model> PageList<T> getList(Class<? extends Model> classZ, String sql, int size, int page, Object[] args);

    /**
     * @param sql
     * @param isComplex 是否复杂SQL
     * @return
     */
    int getCount(String sql, boolean isComplex, Object[] args);
}
