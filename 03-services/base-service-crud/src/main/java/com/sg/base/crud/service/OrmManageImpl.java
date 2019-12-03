package com.sg.base.crud.service;

import com.sg.base.bean.BeanFactory;
import com.sg.base.bean.ContextRefreshedListener;
import com.sg.base.conf.CrudConfiguration;
import com.sg.base.dao.orm.Orm;
import com.sg.base.dao.orm.Query;
import com.sg.base.util.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * CrudOrmManageImpl
 *
 * @author Dai Wenqing
 * @date 2016/3/25
 */
@Component("base.crud.dao.orm.manage")
public class OrmManageImpl implements OrmManage, ContextRefreshedListener {

    @Autowired(required = false)
    private Set<Orm> ormSet;
    @Autowired(required = false)
    private Set<Query> querySet;

    @Autowired
    private CrudConfiguration crudConfiguration;

    private Orm<Query> orm;

    @Override
    public Orm<Query> getOrm() {
        return orm;
    }

    @Override
    public Orm<Query> getOrm(String ormName) {
        if (Validator.isEmpty(ormName)) return orm;
        for (Orm o : ormSet) {
            if (o.getOrmName().equals(ormName))
                return o;
        }
        return orm;
    }

    @Override
    public Query getQuery() {
        return BeanFactory.getBean("base.dao.query." + crudConfiguration.getOrmName());
    }

    @Override
    public Query getQuery(String ormName) {
        if (Validator.isEmpty(ormName)) return BeanFactory.getBean("base.dao.query." + crudConfiguration.getOrmName());
        return BeanFactory.getBean("base.dao.query." + ormName);
    }

    @Override
    public int getContextRefreshedSort() {
        return 6;
    }

    @Override
    public void onContextRefreshed() {
        if (ormSet != null) {
            ormSet.forEach(o -> {
                if (o.getOrmName().equals(crudConfiguration.getOrmName())) {
                    orm = o;
                }
            });
        }
        /*
         * if (Logger.isDebugEnable()) Logger.debug("orm框架初始化完毕，使用的orm是{}",
         * orm.getOrmName());
         */
    }
}
