/**
 *
 */
package com.sg.base.dao.lite;

import com.sg.base.dao.orm.Query;
import com.sg.base.dao.orm.QueryImpl;
import com.sg.base.util.Converter;
import com.sg.base.util.Validator;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

import javax.persistence.Table;

/**
 * Lite检索构造器。用于构造非级联ORM检索语句。
 *
 * @author lpw
 * @see com.sg.base.dao.orm.QueryImpl
 */
@Repository("base.dao.query.lite")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class LiteQuery extends QueryImpl implements Query {
    /*private String select;
    private String from;
    private StringBuffer stringBuffer = new StringBuffer();*/

    public LiteQuery() {
    }

    /**
     * 检索构造器。
     */
    /*public <T extends Model> LiteQuery(Class<T> modelClass) {
        if (modelClass == null)
            throw new NullPointerException("Model类不允许为空！");

        this.modelClass = modelClass;
    }*/
    @Override
    public String toSql() {
        return getQuerySql(this);
    }

    protected String getQuerySql(LiteQuery query) {
        StringBuilder querySql = new StringBuilder().append("SELECT ");
        if (Validator.isEmpty(query.getFrom()))
            return "";
        if (Validator.isEmpty(this.select))
            querySql.append("*");
        else {
            String[] selects = this.select.split(",");
            StringBuffer selectBuffer = new StringBuffer();
            for (int i = 0; i < selects.length; i++) {
                if (i > 0 && selectBuffer.length() > 0) selectBuffer.append(",");
                if ("id".equals(selects[i])) {
                    selectBuffer.append(selects[i]);
                    continue;
                }
                String s = modelTables.get(query.getFrom()).getProperties().get(Converter.toFirstUpperCase(selects[i]));
                if (!Validator.isEmpty(s))
                    selectBuffer.append(s);
            }
            querySql.append(selectBuffer);
        }
        querySql.append(" FROM ").append(query.getFrom().getAnnotation(Table.class).name());
        join(querySql, false);
        if (!Validator.isEmpty(query.getWhere()))
            querySql.append(" WHERE ").append(query.getWhere());
        if (!Validator.isEmpty(query.getGroup()))
            querySql.append(" GROUP BY ").append(query.getGroup());
        if (!Validator.isEmpty(query.getOrder()))
            querySql.append(" ORDER BY ").append(query.getOrder());
        return querySql.toString();
    }
}
