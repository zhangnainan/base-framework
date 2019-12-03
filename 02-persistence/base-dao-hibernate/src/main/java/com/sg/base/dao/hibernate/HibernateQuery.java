package com.sg.base.dao.hibernate;

import com.beust.jcommander.ParameterException;
import com.sg.base.dao.orm.Query;
import com.sg.base.dao.orm.QueryImpl;
import com.sg.base.model.Model;
import com.sg.base.model.enums.Criterion;
import com.sg.base.util.Converter;
import com.sg.base.util.Validator;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Hibernate检索构造器。用于构造HibernateORM检索语句。
 *
 * @author lpw
 */
@Repository("base.dao.query.hibernate")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class HibernateQuery extends QueryImpl implements Query {

    public HibernateQuery() {
        byOrm = true;
    }

    /**
     * 检索构造器。     *
     *
     * @see com.sg.base.dao.orm.QueryImpl
     */
    /*public <T extends Model> HibernateQuery(Class<T> modelClass) {
        this.whereBuffer = new StringBuffer();

        if (modelClass == null)
            throw new NullPointerException("Model类不允许为空！");

        this.modelClass = modelClass;
    }*/
    @Override
    public String toSql() {
        return getQuerySql(false).toString();
    }

    protected StringBuilder getQuerySql(boolean isGettingCount) {
        StringBuilder hql = new StringBuilder();
        aliasMap = new ConcurrentHashMap<>();
        if (!Validator.isEmpty(this.getSelect())) {
            if (!Validator.isEmpty(this.getTo())) {
                String[] selects = this.getSelect().split(",");
                StringBuffer stringBuffer = new StringBuffer();
                for (int i = 0; i < selects.length; i++) {
                    stringBuffer.append(selects[i]);
                    stringBuffer.append(" as ");
                    stringBuffer.append(selects[i]);
                    if (selects.length > 1 && i < selects.length - 1) {
                        stringBuffer.append(",");
                    }
                }
                hql.append("select ").append(stringBuffer.toString());
            }
        }
        from(hql.append(" FROM "), isGettingCount);
        where(hql);

        if (!Validator.isEmpty(this.getGroup()))
            hql.append(" GROUP BY ").append(this.getGroup());
        if (!Validator.isEmpty(this.getOrder()))
            hql.append(" ORDER BY ").append(this.getOrder());
        if (!Validator.isEmpty(this.getQueryContext().get("order")))
            hql.append(this.getQueryContext().get("order"));

        return hql;
    }

    protected void where(StringBuilder hql) {
        if (!Validator.isEmpty(this.getWhere())) {
            String whereStr = this.getWhere();
            for (String k : aliasMap.keySet()) {
                whereStr = whereStr.replace(Converter.toFirstLowerCase(k), aliasMap.get(k));
            }
            hql.append(" WHERE (").append(whereStr).append(")");
        } else
            hql.append(" WHERE ");
        boolean starting = false;

        for (String k : this.getQueryContext().keySet()) {
            if (starting || !Validator.isEmpty(this.getWhere()))
                hql.append(" and ");
            hql.append(k).append(Criterion.Equals.getType());
            // query.getArgs().add(query.getQueryContext().get(k));
            starting = true;
        }
    }

    protected StringBuilder from(StringBuilder hql, boolean isGettingCount) {
        Class<? extends Model> classZ = this.getFrom();
        if (Validator.isEmpty(classZ))
            throw new ParameterException("can not get modelClass");
        hql.append(classZ.getSimpleName());

        tableNameAlias.put(this.getFrom(), "t0");
        hql.append(" as ").append(tableNameAlias.get(this.getFrom()));
        if (!Validator.isEmpty(modelTables)) {
            join(hql, isGettingCount);
        }
        return hql;
    }

}
