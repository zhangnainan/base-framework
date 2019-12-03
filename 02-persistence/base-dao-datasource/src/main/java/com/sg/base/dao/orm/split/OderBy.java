package com.sg.base.dao.orm.split;

import com.sg.base.dao.orm.OrmContext;
import com.sg.base.model.enums.OrderBy;

/**
 * OderBy
 *
 * @author Dai Wenqing
 * @date 2016/5/4
 */
public interface OderBy {
    /**
     * 设置ORDER BY片段。为空则不排序。
     *
     * @param order ORDER BY片段。
     * @return 当前Query实例。
     */
    void order(OrmContext ormContext, String order, OrderBy... orderBy);
}
