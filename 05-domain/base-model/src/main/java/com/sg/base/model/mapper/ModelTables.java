package com.sg.base.model.mapper;

import com.sg.base.model.Model;

/**
 * Model-Table集。用于保存所有Model-Table映射关系表，并提供查询。
 *
 * @author lpw
 */
public interface ModelTables {
    /**
     * 获取Model类对应的Model-Table映射关系表。
     *
     * @param modelClass Model类。
     * @return Model-Table映射关系表。
     */
    ModelTable get(Class<? extends Model> modelClass);

    /**
     * 通过表名查找实体
     *
     * @param tableName 表名
     * @return
     */
    Model get(String tableName);
}
