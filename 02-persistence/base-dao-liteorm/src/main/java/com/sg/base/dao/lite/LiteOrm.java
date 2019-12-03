/**
 *
 */
package com.sg.base.dao.lite;


import com.sg.base.dao.orm.Orm;
import com.sg.base.model.Model;

/**
 * 简单ORM。主要提供高效的ORM，但不提供自动外联合映射的功能。
 *
 * @author lpw
 */
public interface LiteOrm extends Orm<LiteQuery> {
    /**
     * 新增Model。ID由业务系统或数据库控制。
     *
     * @param model 要保存的Model。
     * @return 如果保存成功则返回true；否则返回false。
     */
    <T extends Model> boolean insert(T model);

}
