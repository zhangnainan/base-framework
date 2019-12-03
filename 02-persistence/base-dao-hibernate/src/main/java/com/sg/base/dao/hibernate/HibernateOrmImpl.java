/**
 *
 */
package com.sg.base.dao.hibernate;

import com.sg.base.bean.BeanFactory;
import com.sg.base.dao.Mode;
import com.sg.base.log.Logger;
import com.sg.base.model.Model;
import com.sg.base.model.PageList;
import com.sg.base.model.enums.Criterion;
import com.sg.base.util.Converter;
import com.sg.base.util.Validator;
import net.sf.json.JSONArray;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Iterator;

/**
 * @author lpw
 */
@Repository("base.dao.hibernate")
@Primary
public class HibernateOrmImpl implements HibernateOrm {
    private static final String[] ARG = {"?", ":arg", "arg"};
    @Autowired
    private SessionManage sessionManage;

    @Override
    public String getOrmName() {
        return "hibernate";
    }

    @Override
    public <T extends Model> T findById(Class<T> modelClass, String id, String... datasource) {
        if (Validator.isEmpty(id))
            return null;

        return (T) sessionManage.get(Mode.Read, datasource).get(modelClass, id);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Model> T findOne(HibernateQuery query) {
        query.paging(1, 1);
        PageList<T> pageList = query(query);
        return pageList.getList().size() > 0 ? pageList.getList().get(0) : null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Model> PageList<T> query(HibernateQuery query) {
        PageList<T> models = BeanFactory.getBean(PageList.class);
        if (Validator.isEmpty(query))
            return models;

        query.getQueryContext().forEach((k, v) -> {
            query.getArgs().add(v);
        });

        // 每页面总条件大于0，且当前不是获取第一页
        if (query.getPage() > 0) {
            models.setPage(Converter.toInt(createCountQuery(Mode.Read, query).iterate().next()), query.getSize(), query.getPage());
        }

        if (!Validator.isEmpty(query.getTo())) {
            try {
                models.setList(createCommonQuery(Mode.Read, query).setResultTransformer(Transformers.aliasToBean(query.getTo())).list());
            } catch (Exception e) {
                throw new QueryCastException("类型转换出错，转须指明To(class),且查询要加上select(ToModel全部字段，以逗号隔开)");
            }
        } else
            models.setList(createCommonQuery(Mode.Read, query).list());

        return models;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Model> Iterator<T> iterate(HibernateQuery query) {
        if (Validator.isEmpty(query.getTo()))
            return createCommonQuery(Mode.Read, query).iterate();
        return createCommonQuery(Mode.Read, query).setResultTransformer(Transformers.aliasToBean(query.getTo())).iterate();
    }

    @Override
    public int count(HibernateQuery query) {
        query.getQueryContext().forEach((k, v) -> {
            query.getArgs().add(v);
        });
        return Converter.toInt(createCountQuery(Mode.Read, query).iterate().next());
    }

    @Override
    public <T extends Model> boolean save(T model, String... datasource) {
        if (model == null) {
            Logger.warn(null, "要保存的Model为null！");
            return false;
        }

        if (Validator.isEmpty(model.getId()))
            model.setId(null);
        Object object = null;
        //model.setId(mo);
        Session session = sessionManage.get(Mode.Write, datasource);
        try {
            session.saveOrUpdate(model);
        } catch (Exception e) {
            object = session.merge(model);
        }
        if (!Validator.isEmpty(object)) {
            if (object instanceof Model) {
                model.setId(Model.class.cast(object).getId());
            }
        }
        return true;
    }

    @Override
    public boolean update(HibernateQuery query) {
        if (query.getSet().keySet().size() < 1)
            return false;
        StringBuilder hql = query.from(new StringBuilder().append("UPDATE "), false).append(" set ");
        int ndx = 0;
        for (String k : query.getSet().keySet()) {
            if (ndx > 0)
                hql.append(",");
            hql.append(k).append(Criterion.Equals.getType());
            ndx++;
            query.getArgs().add(query.getArgs().size() - 1, query.getSet().get(k));
        }
        /*if (!Validator.isEmpty(query.getWhere()))
            hql.append(" WHERE ").append(query.getWhere());*/
        query.where(hql);
        Query q = sessionManage.get(Mode.Write, query.getDatasource()).createQuery(replaceArgs(hql));
        setParameters(query, q);
        return q.executeUpdate() >= 0;
    }

    @Override
    public <T extends Model> boolean delete(T model, String... datasource) {
        sessionManage.get(Mode.Write, datasource).delete(model);
        return true;
    }

    @Override
    public boolean delete(HibernateQuery query) {
        StringBuilder hql = query.from(new StringBuilder().append("DELETE "), false);
        if (!Validator.isEmpty(query.getWhere()))
            hql.append(" WHERE ").append(query.getWhere());

        createCommonQuery(Mode.Write, query).executeUpdate();

        return true;
    }

    @Override
    public JSONArray getAsJson(HibernateQuery query) {
        return null;
    }

    protected Query createCommonQuery(Mode mode, HibernateQuery hQuery) {
        StringBuilder hql = hQuery.getQuerySql(false);

        Query query = sessionManage.get(mode, hQuery.getDatasource()).createQuery(replaceArgs(hql));
        if (hQuery.getSize() > 0)
            query.setFirstResult(hQuery.getSize() * (hQuery.getPage() - 1)).setMaxResults(hQuery.getSize());
        setParameters(hQuery, query);
        return query;

    }

    protected Query createCountQuery(Mode mode, HibernateQuery hQuery) {
        StringBuilder hql = hQuery.from(new StringBuilder("SELECT COUNT(*) from "), true);
        hQuery.where(hql);
        if (Logger.isDebugEnable())
            Logger.debug("sql:{}", hql);
        Query query = sessionManage.get(mode, hQuery.getDatasource()).createQuery(replaceArgs(hql));
        setParameters(hQuery, query);
        return query;
    }

    protected void setParameters(HibernateQuery hQuery, Query query) {
        Object[] args = hQuery.getArgs().toArray();
        for (int i = 0; i < args.length; i++) {
            if (args[i] == null)
                query.setParameter(ARG[2] + i, args[i]);
            else if (args[i] instanceof Collection<?>)
                query.setParameterList(ARG[2] + i, (Collection<?>) args[i]);
            else if (args[i].getClass().isArray())
                query.setParameterList(ARG[2] + i, (Object[]) args[i]);
            else
                query.setParameter(ARG[2] + i, args[i]);
        }
    }

    protected String replaceArgs(StringBuilder hql) {
        for (int i = 0, position; (position = hql.indexOf(ARG[0])) > -1; i++)
            hql.replace(position, position + 1, ARG[1] + i);

        return hql.toString();
    }
}
