package com.sg.base.dao.orm;

import com.sg.base.model.Model;
import com.sg.base.model.enums.JoinType;
import com.sg.base.util.Validator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author lpw
 */
public class OrmContextImpl implements OrmContext {
    //protected Class<? extends Model> modelClass;
    //操作对象类型
    protected Class<? extends Model> fromModelClass;
    //主要用于非关系型
    protected String formType;
    //查询结果转换类型
    protected Class<?> to;
    protected Map<String, Object> set = new ConcurrentHashMap<>();
    //查询字段
    protected String select;
    //条件
    protected StringBuffer whereBuffer = new StringBuffer();
    protected List<WhereContext> whereContexts = new ArrayList<>();
    protected String group;
    protected StringBuffer order = new StringBuffer();
    protected int size = -1;
    protected int page = -1;
    protected Map<String, Object> queryContext = new ConcurrentHashMap<>();
    protected List<Object> args = new ArrayList<>();
    protected String as;
    //数据源
    protected String datasource = "";
    protected String sql = "";
    protected Map<Class<? extends Model>, JoinType> joinClasses = new ConcurrentHashMap<>();
    protected String[] symbol = new String[]{"\0", "\b", "\n", "\"", "\r", "\t", "\\", "%", "_", "'"};
    //字段是否经过Orm进行转换，如果有经过Orm框架，则在拼装sql的时候使用对象，否则需要对字段进行转换
    protected boolean byOrm = false;
    //表别名
    protected Map<Class<? extends Model>, String> tableNameAlias = new ConcurrentHashMap<>();
    protected String schema;

    /*@Override
    public Class<? extends Model> getModelClass() {
        return modelClass;
    }*/

    @Override
    public Map<String, Object> getSet() {
        return set;
    }

    public String getWhere() {
        return whereBuffer.toString();
    }

    @Override
    public String getGroup() {
        return group;
    }

    @Override
    public String getOrder() {
        return order.toString();
    }

    @Override
    public String getSql() {
        return sql;
    }

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public int getPage() {
        return page;
    }

    @Override
    public String getSelect() {
        return select;
    }

    @Override
    public Class<?> getTo() {
        return to;
    }

    @Override
    public Class<? extends Model> getFrom() {
        return fromModelClass;
    }

    @Override
    public String getFromType() {
        return this.formType;
    }

    public void setContext(String key, Object value) {
        if (!Validator.isEmpty(key) || !Validator.isEmpty(value))
            this.queryContext.put(key, value);
    }

    @Override
    public Map<String, Object> getQueryContext() {
        return this.queryContext;
    }

    @Override
    public Map<Class<? extends Model>, JoinType> getJoinClasses() {
        return joinClasses;
    }

    @Override
    public List<WhereContext> getWhereContexts() {
        return this.whereContexts;
    }

    @Override
    public String getSchema() {
        return this.schema;
    }

    @Override
    public List<Object> getArgs() {
        return this.args;
    }

    public String getAs() {
        return this.as;
    }

    @Override
    public String getDatasource() {
        return this.datasource;
    }


}
