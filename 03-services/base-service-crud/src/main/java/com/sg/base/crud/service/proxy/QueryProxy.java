package com.sg.base.crud.service.proxy;

import com.sg.base.crud.service.QueryService;
import com.sg.base.dao.orm.OrmContext;
import com.sg.base.dao.orm.Query;
import com.sg.base.model.Model;
import com.sg.base.model.PageList;
import com.sg.base.model.enums.Criterion;
import com.sg.base.model.enums.JoinType;
import com.sg.base.model.enums.Operator;
import com.sg.base.model.enums.OrderBy;

/**
 * 提供一个全方位的，更灵活的查询操作
 *
 * @author Dai Wenqing
 * @date 2016/3/31
 */
public class QueryProxy {
    private Query query;
    private QueryService queryService;

    public QueryProxy(Query query, QueryService queryService) {
        this.query = query;
        this.queryService = queryService;
    }

    public QueryProxy select(String select) {
        query.select(select);
        return this;
    }

    public QueryProxy from(Class<? extends Model> from, String... as) {
        query.from(from);
        return this;
    }

    public QueryProxy to(Class<?> to) {
        query.to(to);
        return this;
    }

    public QueryProxy join(Class<? extends Model> classZ, JoinType joinType) {
        query.join(classZ, joinType);
        return this;
    }

    public QueryProxy where(String column, Criterion criterion, Object value, Operator... operator) {
        query.where(column, criterion, value, operator);
        return this;
    }

    public QueryProxy where(String column, Object value, Operator... operator) {
        where(column, Criterion.Equals, value, operator);
        return this;
    }

    public QueryProxy where(String where) {
        query.where(where);
        return this;
    }

    public QueryProxy order(String order, OrderBy... orderBy) {
        query.order(order, orderBy);
        return this;
    }

    public QueryProxy group(String group) {
        query.group(group);
        return this;
    }

    public QueryProxy paging(int page, int size) {
        query.paging(page, size);
        return this;
    }

    /**
     * 获取sql语句
     *
     * @return
     */
    public String getSql() {
        return query.toSql();
    }

    public Object[] getArgs() {
        OrmContext ormContext = OrmContext.class.cast(query);
        return ormContext.getArgs().toArray();
    }

    public <T extends Model> PageList<T> list(Object... args) {
        return list(false, args);
    }

    public <T extends Model> PageList<T> list(boolean excludeDomain, Object... args) {
        // QueryInfo queryInfo = QueryInfo.class.cast(query);
        return queryService.list(query, excludeDomain, args);
    }

    public <T extends Model> T one(Object... args) {
        return one(false, args);
    }

    public <T extends Model> T one(boolean excludeDomain, Object... args) {
        return queryService.one(query, excludeDomain, args);
    }

    public <T extends Model> PageList<T> all(Object... args) {
        return all(false, args);
    }

    public <T extends Model> PageList<T> all(boolean excludeDomain, Object... args) {
        // QueryInfo queryInfo = QueryInfo.class.cast(query);
        return queryService.all(query, excludeDomain, args);
    }

    public int count(Object... args) {
        return queryService.count(query, false, args);
    }
}
