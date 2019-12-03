package com.sg.base.dao.hibernate.dialect;

import com.sg.base.dao.dialect.MysqlDialect;
import org.springframework.stereotype.Repository;

/**
 * MysqlHDialect
 *
 * @author Dai Wenqing
 * @date 2016/1/23
 */
@Repository("base.dao.hibernate.dialect.mysql")
public class MysqlHDialect extends MysqlDialect implements HDialect {
    @Override
    public String getHibernateDialect() {
        return "org.hibernate.dialect.MySQLDialect";
    }
}
