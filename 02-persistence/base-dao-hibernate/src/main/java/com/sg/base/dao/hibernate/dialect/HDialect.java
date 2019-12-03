package com.sg.base.dao.hibernate.dialect;

import com.sg.base.dao.dialect.Dialect;

/**
 * HDialect
 *
 * @author Dai Wenqing
 * @date 2016/1/23
 */
public interface HDialect extends Dialect {
    String getHibernateDialect();
}
