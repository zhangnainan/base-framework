/**
 *
 */
package com.sg.base.dao.dialect;

import org.springframework.stereotype.Repository;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author lpw
 */
@Repository("base.dao.dialect.mysql")
public class MysqlDialect implements Dialect {
    @Override
    public String getName() {
        return "mysql";
    }

    @Override
    public String getDriver() {
        return "com.mysql.jdbc.Driver";
    }

    @Override
    public String getUrl(String ip, String schema) {
        return "jdbc:mysql://" + ip + "/" + schema + "?useUnicode=true&characterEncoding=utf-8";
    }

    @Override
    public String getValidationQuery() {
        return "SELECT CURRENT_DATE";
    }

    /*
     * @Override public String getHibernateDialect() { return
     * "org.hibernate.dialect.MySQLDialect"; }
     */

    @Override
    public String appendPagination(String sql, int size, int page) {
        StringBuffer buffer = new StringBuffer(sql);
        buffer.append(" LIMIT ").append(size * (page - 1)).append(',').append(size);
        return buffer.toString();
    }

    @Override
    public String getDateConverter(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return "to_date('{" + formatter.format(date) + "}','%Y-%m-%d %H:%i:%s')";
    }
}
