package com.sg.base.dao.hibernate.dialect;

import com.sg.base.dao.dialect.OracleDialect;
import org.springframework.stereotype.Repository;

/**
 * OracleHDialect
 *
 * @author Dai Wenqing
 * @date 2016/1/23
 */
@Repository("base.dao.hibernate.dialect.oracle")
public class OracleHDialect extends OracleDialect implements HDialect {
    @Override
    public String getHibernateDialect() {
        return "org.hibernate.dialect.Oracle10gDialect";
    }
}
