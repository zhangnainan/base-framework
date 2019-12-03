package com.sg.base.dao.orm.split;

import com.sg.base.dao.orm.OrmContext;
import com.sg.base.model.enums.Criterion;
import com.sg.base.model.enums.Operator;

/**
 * Where
 *
 * @author Dai Wenqing
 * @date 2016/5/4
 */
public interface Where {
    /**
     * 设置WHERE片段。
     *
     * @param column 字段
     * @return 当前Query实例。
     */

    void where(OrmContext ormContext, String column, Criterion criterion, Object[] value, Operator... operator);
}
