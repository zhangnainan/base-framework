package com.sg.base.dao.orm;

import com.sg.base.model.Model;
import com.sg.base.model.PageList;
import net.sf.json.JSONArray;

/**
 * 规范ORM框架 对象提示的通用接口 对不同ORM要求实现的接口
 *
 * @author lpw
 */
public interface Orm<Q extends Query> {
    /**
     * 获取当前正在使用的orm框架
     *
     * @return
     */
    String getOrmName();

    /**
     * 根据ID值获取Model实例。
     *
     * @param modelClass Model类。
     * @param id         ID值。
     * @return Model实例，如果不存在则返回null。
     */
    <T extends Model> T findById(Class<T> modelClass, String id, String... datasource);

    /**
     * 检索一条满足条件的数据。如果存在多条满足条件的数据则只返回第一条数据。
     *
     * @param query 检索条件。
     * @return Model实例，如果不存在则返回null。
     */
    <T extends Model> T findOne(Q query);

    /**
     * 检索满足条件的数据。
     *
     * @param query 检索条件。
     * @return Model实例集。
     */
    <T extends Model> PageList<T> query(Q query);

    /**
     * 计算满足条件的数据数。
     *
     * @param query 检索条件。
     * @return 数据数。
     */
    int count(Q query);

    /**
     * 保存Model。如果要保存Model实例的ID为null则执行新增操作，否则执行更新操作。新增时将自动创建一个随机ID。
     *
     * @param model 要保存的Model。
     * @return 如果保存成功则返回true；否则返回false。
     */
    <T extends Model> boolean save(T model, String... datasource);

    /**
     * 批量更新数据。
     *
     * @param query 更新条件。
     * @return 如果删除成功则返回true；否则返回false。
     */
    boolean update(Q query);

    /**
     * 删除Model。
     *
     * @param model 要删除的Model。
     * @return 如果删除成功则返回true；否则返回false。
     */
    <T extends Model> boolean delete(T model, String... datasource);

    /**
     * 批量删除数据。
     *
     * @param query 删除条件。
     * @return 如果删除成功则返回true；否则返回false。
     */
    boolean delete(Q query);

    JSONArray getAsJson(Q query);

}
