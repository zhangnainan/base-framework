package com.sg.base.crud.service;

import com.sg.base.dao.orm.Orm;
import com.sg.base.dao.orm.Query;

/**
 * 获取orm框架，当前
 *
 * @author Dai Wenqing
 * @date 2016/3/25
 */
public interface OrmManage {
    Orm<Query> getOrm();

    Orm<Query> getOrm(String ormName);

    Query getQuery();

    Query getQuery(String ormName);
}
