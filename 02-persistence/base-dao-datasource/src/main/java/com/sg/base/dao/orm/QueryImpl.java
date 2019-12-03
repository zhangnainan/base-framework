package com.sg.base.dao.orm;

import com.beust.jcommander.ParameterException;
import com.sg.base.model.Model;
import com.sg.base.model.enums.Criterion;
import com.sg.base.model.enums.JoinType;
import com.sg.base.model.enums.Operator;
import com.sg.base.model.enums.OrderBy;
import com.sg.base.model.mapper.ModelTables;
import com.sg.base.util.Converter;
import com.sg.base.util.Validator;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.JoinColumn;
import javax.persistence.MappedSuperclass;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 查询基类
 *
 * @author Dai Wenqing
 * @date 2016/9/2
 */
@MappedSuperclass
public class QueryImpl extends OrmContextImpl implements Query {
    @Autowired
    protected ModelTables modelTables;
    protected Map<String, String> aliasMap = new ConcurrentHashMap<>();

    @Override
    public Query select(String select) {
        this.select = select;
        return this;
    }

    @Override
    public Query schema(String schema) {
        this.schema = schema;
        return this;
    }

    @Override
    public Query from(Class<? extends Model> from, String... as) {
        this.fromModelClass = from;
        if (as.length > 0)
            this.as = as[0];
        return this;
    }

    @Override
    public Query from(String type) {
        this.formType = type;
        return this;
    }

    @Override
    public Query set(String column, Object value) {
        this.set.put(column, value);
        return this;
    }

    @Override
    public Query to(Class<?> to) {
        this.to = to;
        return this;
    }

    @Override
    public Query join(Class<? extends Model> classZ, JoinType joinType) {
        if (!Validator.isEmpty(classZ))
            joinClasses.put(classZ, joinType);
        return this;
    }

    @Override
    public Query datasource(String datasource) {
        this.datasource = datasource;
        return this;
    }

    @Override
    public Query where(String column, Object value, Operator... operator) {
        return where(column, Criterion.Equals, value, operator);
    }

    /**
     * 设置WHERE片段。
     *
     * @param column 字段名。
     * @return 当前Query实例。
     */
    @Override
    public Query where(String column, Criterion criterion, Object value, Operator... operator) {
        if (Validator.isEmpty(column))
            return this;
        if (Validator.isEmpty(value))
            return this;

        WhereContext whereContext = new WhereContext();
        whereContext.setCriterion(criterion);
        whereContext.setKey(column);
        this.whereContexts.add(whereContext);

        if (this.whereBuffer.length() > 0) {
            if (operator.length < 1) {
                this.whereBuffer.append(" and ");
                whereContext.setOperator(Operator.And);
            } else {
                this.whereBuffer.append(" " + operator[0].getType() + " ");
                whereContext.setOperator(operator[0]);
            }
        }
        if (!Validator.isEmpty(getAs())) {
            if (!column.contains(getAs())) {
                this.whereBuffer.append(getAs() + ".");
            }
        }
        if (byOrm || column.equals("id"))
            this.whereBuffer.append(column);
        else if (!Validator.isEmpty(this.getFrom()))
            this.whereBuffer.append(modelTables.get(this.getFrom()).getProperties().get(Converter.toFirstUpperCase(column)));
        if (!Validator.isEmpty(criterion))
            this.whereBuffer.append(criterion.getType());
        if (criterion == Criterion.In || criterion == Criterion.NotIN) {

            String[] valueSplit = value.toString().split(",");
            this.whereBuffer.append("(");
            boolean add = false;
            for (int i = 0; i < valueSplit.length; i++) {
                if (add && i > 0)
                    this.whereBuffer.append(",");
                this.whereBuffer.append("?");
                add = true;
            }
            this.whereBuffer.append(")");
        }

        if (criterion == Criterion.Like) {
            if (value.toString().contains("%"))
                value = value.toString().replace("%", "\\" + value);
            else if (value.toString().equals("_"))
                value = value.toString().replace("_", "\\" + value);
            args.add("%" + value + "%");
            whereContext.setValue(value);
        } else if (criterion == Criterion.StartsWith) {
            args.add(value + "%");
            whereContext.setValue(value);
        } else if (criterion == Criterion.EndsWith) {
            args.add("%" + value);
            whereContext.setValue(value);
        } else if (criterion == Criterion.Between) {
            if (value.getClass().isArray()) {
                Object[] values = (Object[]) value;
                if (values.length > 1) {
                    args.add(values[0]);
                    args.add(values[1]);
                    whereContext.setValue(values[0], values[1]);
                } else
                    throw new ParameterException("参数个数不对，对于Between and 需要2个参数");

            } else {
                String[] valueSplit = value.toString().split(",");
                if (valueSplit.length > 1) {
                    args.add(valueSplit[0]);
                    args.add(valueSplit[1]);
                    whereContext.setValue(valueSplit[0], valueSplit[1]);
                } else {
                    throw new ParameterException("参数个数不对，对于Between and 需要2个参数");
                }
            }
        } else if (criterion == Criterion.In || criterion == Criterion.NotIN) {
            String[] valueSplit = value.toString().split(",");
            for (String v : valueSplit) {
                args.add(v);
            }
        } else {
            args.add(value);
            whereContext.setValue(value);
        }
        return this;
    }

    @Override
    public Query where(String where) {
        this.whereBuffer.append(where);
        return this;
    }

    /**
     * 设置GROUP BY片段。为空则不分组。
     *
     * @param group GROUP BY片段。
     * @return 当前Query实例。
     */
    @Override
    public Query group(String group) {
        this.group = group;

        return this;
    }

    /**
     * 设置ORDER BY片段。为空则不排序。
     *
     * @param order ORDER BY片段。
     * @return 当前Query实例。
     */
    @Override
    public Query order(String order, OrderBy... orderBy) {
        if (this.order.length() > 0)
            this.order.append(",");
        if (orderBy.length > 0)
            this.order.append(order).append(" " + orderBy[0].getType() + " ");
        return this;
    }

    /**
     * 设置当前显示的页码。只有当size大于0时页码数才有效。如果页码小于1，则默认为1。
     *
     * @param page 当前显示的页码。
     * @return 当前Query实例。
     */
    @Override
    public Query paging(int page, int size) {
        this.page = page;
        this.size = size;
        return this;
    }

    @Override
    public String toSql() {
        return "";
    }

    /**
     * 关联查询
     *
     * @param hql
     * @param isGettingCount
     */
    protected void join(StringBuilder hql, boolean isGettingCount) {
        int ndx = 1;
        //获取包含JoinColumn注解的所有类型
        Map<Class<? extends Model>, JoinColumn> joinColumnMap = modelTables.get(this.getFrom()).getJoinColumn();
        //对类型设置别名
        for (Class<? extends Model> c : this.getJoinClasses().keySet()) {
            tableNameAlias.put(c, "t" + ndx);
            ndx++;
        }
        //记录已经做了关联的类型
        List<Class<? extends Model>> hasJoinClassList = new ArrayList<>();
        hasJoinClassList.add(this.getFrom());
        if (this.getJoinClasses().size() > 0) {
            for (Class<? extends Model> c : this.getJoinClasses().keySet()) {
                JoinColumn joinColumn = joinColumnMap.get(c);
                Class<? extends Model> joinModelClass = this.getFrom();
                if (Validator.isEmpty(joinColumn))
                    joinModelClass = getJoinModelClass(c);
                join(joinModelClass, c, hql, this.getJoinClasses().get(c), joinColumn, hasJoinClassList, isGettingCount);
            }
        }
    }

    private Class<? extends Model> getJoinModelClass(Class<? extends Model> forJoinClass) {
        for (Class<? extends Model> c : this.getJoinClasses().keySet()) {
            Map<Class<? extends Model>, JoinColumn> joinColumnMap = modelTables.get(c).getJoinColumn();
            JoinColumn joinColumn = joinColumnMap.get(forJoinClass);
            if (!Validator.isEmpty(joinColumn))
                return c;
        }
        return null;
    }

    //  FROM UserModel as t0 inner join  open t0.school as t1 WHERE

    private void join(Class<? extends Model> OrinClass, Class<? extends Model> forJoinClass,
                      StringBuilder hql, JoinType joinType, JoinColumn forJoinColumn,
                      List<Class<? extends Model>> hasJoinClassList, boolean isGettingCount) {
        // 此表已经做了关联
        if (hasJoinClassList.contains(forJoinClass))
            return;
        // 原始表都未做关联，需要先关联原表
        if (!hasJoinClassList.contains(OrinClass))
            join(this.getFrom(), OrinClass, hql, this.getJoinClasses().get(OrinClass)
                    , forJoinColumn, hasJoinClassList, isGettingCount);
        if (!Validator.isEmpty(OrinClass)) {
            String fetch = " ";
            if (!isGettingCount)
                fetch = " fetch ";
            String forAlias = tableNameAlias.get(forJoinClass);
            String originAlias = tableNameAlias.get(OrinClass);
            aliasMap.put(forJoinClass.getSimpleName(), tableNameAlias.get(forJoinClass));
            hql.append(joinType.getType());
            if (this.byOrm)
                hql.append(fetch);
            hql.append(tableNameAlias.get(OrinClass)).append(".");
            if (this.byOrm)
                hql.append(Converter.toFirstLowerCase(forJoinClass.getSimpleName()));
            else
                hql.append(forJoinClass.getAnnotation(Table.class).name());
            hql.append(" as ").append(forAlias);
            if (!this.byOrm) {
                hql.append(" on ");
                hql.append(forAlias).append(".id =").append(originAlias).append(".").append(forJoinColumn.name());
            }
            hasJoinClassList.add(forJoinClass);
        }
    }

}
