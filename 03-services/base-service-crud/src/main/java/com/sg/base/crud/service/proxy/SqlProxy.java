package com.sg.base.crud.service.proxy;

import com.sg.base.bean.BeanFactory;
import com.sg.base.dao.sql.Sql;
import com.sg.base.model.Model;
import com.sg.base.model.PageList;
import com.sg.base.util.Validator;
import net.sf.json.JSONArray;
import org.apache.commons.lang.NullArgumentException;

/**
 * SqlProxy
 *
 * @author Dai Wenqing
 * @date 2016/7/15
 */
public class SqlProxy {
    private String sql = "";
    private int page = -1;
    private int size = -1;
    private Class<? extends Model> classZ;

    public SqlProxy(Class<? extends Model> classZ, String sql) {
        this.classZ = classZ;
        this.sql = sql;
    }

    /*
     * public SqlProxy sql(Class<? extends Model> classZ, String sql) {
     * this.classZ = classZ; this.sql = sql; return this; }
     */

    public SqlProxy(String tableName, String sql) {
        this.sql = sql.replace("#", tableName);
    }

    public SqlProxy(String sql) {
        this.sql = sql;
    }

    public SqlProxy paging(int page, int size) {
        this.page = page;
        this.size = size;
        return this;
    }

    public <T extends Model> PageList<T> list(Object... args) {
        Sql sqlOrm = BeanFactory.getBean(Sql.class);
        return sqlOrm.getList(this.classZ, this.sql, size, page, args);
    }

    public <T extends Model> T one(Object... args) {
        Sql sqlOrm = BeanFactory.getBean(Sql.class);
        PageList<T> pageList = sqlOrm.getList(this.classZ, this.sql, -1, -1, args);
        if (pageList.getList().size() > 0)
            return pageList.getList().get(0);
        return null;
    }

    public JSONArray asJson(Object... args) {
        Sql sqlOrm = BeanFactory.getBean(Sql.class);
        return sqlOrm.queryAsJson(this.sql, size, page, args);
    }

    public int count(Object... args) {
        Sql sqlOrm = BeanFactory.getBean(Sql.class);
        return sqlOrm.getCount(this.sql, true, args);
    }

    public int count2x(Object... args) {
        Sql sqlOrm = BeanFactory.getBean(Sql.class);
        return sqlOrm.getCount(this.sql, false, args);
    }

    public boolean invoke(Object... args) {
        if (!Validator.isEmpty(sql)) {
            Sql sqlOrm = BeanFactory.getBean(Sql.class);
            return sqlOrm.execute(sql, args);
        } else
            throw new NullArgumentException("sql");

    }
}
