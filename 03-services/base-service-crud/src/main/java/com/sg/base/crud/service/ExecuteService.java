package com.sg.base.crud.service;

import com.sg.base.crud.enums.DeleteType;
import com.sg.base.dao.orm.Query;
import com.sg.base.model.Model;

/**
 * 删除服务
 *
 * @author Dai Wenqing
 * @date 2016/7/15
 */
public interface ExecuteService {


    //<T extends Model> Boolean remove(Class<T> classZ, String id);
    // <T extends Model> Boolean remove(Class<T> classZ, Query query);


    //<T extends Model> Boolean recycle(Class<T> classZ, String id);


    //<T extends Model> Boolean delete(Class<T> classZ, String id);

    // <T extends Model> Boolean delete(Class<T> classZ, Query query);

    //<T extends Model> Boolean clear(Class<T> classZ, Map<String, Criterion> criterionMap, Object[] args);

    <T extends Model> boolean deleteOrRecycle(Query query, DeleteType deleteType);

    boolean update(Query query, Object... args);
}
