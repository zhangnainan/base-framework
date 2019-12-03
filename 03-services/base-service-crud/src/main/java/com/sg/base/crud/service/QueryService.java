package com.sg.base.crud.service;

import com.sg.base.dao.orm.Query;
import com.sg.base.model.Model;
import com.sg.base.model.PageList;
import com.sg.base.model.enums.Criterion;
import net.sf.json.JSONArray;

import java.util.Map;

/**
 * 查询服务
 *
 * @author Dai Wenqing
 * @date 2016/7/15
 */
public interface QueryService {

    /**
     * 使用Query上下文查询单个有效的数据
     *
     * @param query         查询上下文
     * @param excludeDomain 是否排除域的过滤
     * @param args          参数
     * @param <T>
     * @return
     */
    <T extends Model> T one(Query query, boolean excludeDomain, Object... args);

    /**
     * 使用Query上下文查询所有有效的数据
     *
     * @param query         查询上下文
     * @param excludeDomain 是否排除域的过滤
     * @param args          参数
     * @param <T>
     * @return
     */
    <T extends Model> PageList<T> list(Query query, boolean excludeDomain, Object... args);

    /**
     * 使用Query上下文查询所有的数据，包括无效数据
     *
     * @param query         查询上下文
     * @param excludeDomain 是否排除域的过滤
     * @param args          参数
     * @param <T>
     * @return
     */
    <T extends Model> PageList<T> all(Query query, boolean excludeDomain, Object... args);

    /**
     * 计算总条件
     *
     * @param query
     * @param excludeDomain
     * @param args
     * @return
     */
    int count(Query query, boolean excludeDomain, Object... args);

    JSONArray asJson(Query query, String ormName);

    Query mapToQuery(Class<? extends Model> classZ, Map<String, Criterion> criterionMap, int page, int size, Object... args);
}
