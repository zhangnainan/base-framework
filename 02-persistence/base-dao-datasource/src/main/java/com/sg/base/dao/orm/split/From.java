package com.sg.base.dao.orm.split;

import com.sg.base.dao.orm.OrmContext;
import com.sg.base.model.Model;

/**
 * From
 *
 * @author Dai Wenqing
 * @date 2016/5/4
 */
public interface From {
    /**
     * 设置FROM表名称集，至少必须包含一个表名称。如果为空则使用Model类对应的表名称。
     *
     * @param from FROM 对象类型。
     * @param as   别名
     * @return 当前Query实例。
     */
    void from(OrmContext ormContext, Class<? extends Model> from, String... as);
}
