package com.sg.base.dao.orm.split;

import com.sg.base.dao.orm.OrmContext;

/**
 * GroupBy
 *
 * @author Dai Wenqing
 * @date 2016/5/4
 */
public interface GroupBy {
    void group(OrmContext ormContext, String group);
}
