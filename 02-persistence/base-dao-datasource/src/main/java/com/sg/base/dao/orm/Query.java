package com.sg.base.dao.orm;

import com.sg.base.model.Model;
import com.sg.base.model.enums.Criterion;
import com.sg.base.model.enums.JoinType;
import com.sg.base.model.enums.Operator;
import com.sg.base.model.enums.OrderBy;

/**
 * 检索接口。
 *
 * @author lpw
 */
public interface Query {

    /**
     * 设置SELECT字段集，如果为空则为所有字段。
     *
     * @param select SELECT字段集。
     * @return 当前Query实例。
     */
    Query select(String select);

    Query schema(String schema);

    /**
     * 设置FROM表名称集，至少必须包含一个表名称。如果为空则使用Model类对应的表名称。
     *
     * @param from FROM 对象类型。
     * @param as   别名
     * @return 当前Query实例。
     */
    Query from(Class<? extends Model> from, String... as);

    Query from(String type);

    Query set(String column, Object value);

    Query to(Class<?> to);

    Query datasource(String datasource);

    //Query set(String set);

    //Query sql(String sql);

    Query join(Class<? extends Model> classZ, JoinType joinType);

    /**
     * 设置WHERE片段。
     *
     * @param column 字段
     * @return 当前Query实例。
     */
    Query where(String column, Criterion criterion, Object value, Operator... operator);

    Query where(String column, Object value, Operator... operator);

    Query where(String where);

    /**
     * 设置ORDER BY片段。为空则不排序。
     *
     * @param order ORDER BY片段。
     * @return 当前Query实例。
     */
    Query order(String order, OrderBy... orderBy);

    /**
     * 设置GROUP BY片段。为空则不分组。
     *
     * @param group GROUP BY片段。
     * @return 当前Query实例。
     */
    Query group(String group);

    /**
     * 设置最大返回的记录数。如果小于1则返回全部数据。
     *
     * @param size
     *            最大返回的记录数。
     * @return 当前Query实例。
     */
    // Query size(int size);

    /**
     * 设置当前显示的页码。只有当size大于0时页码数才有效。如果页码小于1，则默认为1。
     *
     * @param page 当前显示的页码。
     * @param size 每页面多少条
     * @return 当前Query实例。
     */
    Query paging(int page, int size);

    /**
     * 转化为sql语句
     *
     * @return
     */
    String toSql();

    /**
     * 根据orm获取相应的queryName
     * @return
     */
    //String getName();
}
