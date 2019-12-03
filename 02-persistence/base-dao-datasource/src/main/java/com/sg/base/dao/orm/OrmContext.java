package com.sg.base.dao.orm;

import com.sg.base.model.Model;
import com.sg.base.model.enums.JoinType;

import java.util.List;
import java.util.Map;

/**
 * QueryInfo
 *
 * @author Dai Wenqing
 * @date 2016/3/25
 */
public interface OrmContext {

    /**
     * 获取Model类。
     *
     * @return Model类。
     */
    //Class<? extends Model> getModelClass();

    /**
     * 获取数据源。
     *
     * @return 数据源。
     */
    // String getDataSource();

    /**
     * 获取SET片段。
     *
     * @return SET片段。
     */
    Map<String, Object> getSet();

    /**
     * 获取WHERE片段。
     *
     * @return WHERE片段。
     */
    String getWhere();

    /**
     * 获取GROUP BY片段。
     *
     * @return GROUP BY片段。
     */
    String getGroup();

    /**
     * 获取ORDER BY片段。
     *
     * @return ORDER BY片段。
     */
    String getOrder();

    String getSql();

    /**
     * 获取最大返回的记录数。
     *
     * @return 最大返回的记录数。
     */
    int getSize();

    /**
     * 获取当前显示的页码。
     *
     * @return 当前显示的页码。
     */
    int getPage();

    String getSelect();

    Class<?> getTo();

    Class<? extends Model> getFrom();

    /**
     * 非关系型文件类型
     *
     * @return
     */
    String getFromType();

    String getAs();

    String getDatasource();

    Map<String, Object> getQueryContext();

    List<Object> getArgs();

    Map<Class<? extends Model>, JoinType> getJoinClasses();

    List<WhereContext> getWhereContexts();

    String getSchema();

}
