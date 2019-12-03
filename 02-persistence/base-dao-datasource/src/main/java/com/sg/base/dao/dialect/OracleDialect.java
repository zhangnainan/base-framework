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
@Repository("base.dao.dialect.oracle")
public class OracleDialect implements Dialect {
    @Override
    public String getName() {
        return "oracle";
    }

    @Override
    public String getDriver() {
        return "oracle.jdbc.driver.OracleDriver";
    }

    @Override
    public String getUrl(String ip, String schema) {
        return "jdbc:oracle:thin:@" + ip + ":" + schema;
    }

    @Override
    public String getValidationQuery() {
        return "SELECT SYSDATE FROM DUAL";
    }

    /*
     * @Override public String getHibernateDialect() { return
     * "org.hibernate.dialect.Oracle10gDialect"; }
     */

    @Override
    public String appendPagination(String sql, int size, int page) {
        StringBuffer buffer = new StringBuffer(sql);
        buffer.insert(0, "SELECT * FROM (SELECT oracle_pagination_1.*, ROWNUM AS rowno FROM (").append(") oracle_pagination_1 WHERE ROWNUM<=")
                .append(size * page).append(") oracle_pagination_2 WHERE oracle_pagination_2.rowno>").append(size * (page - 1));
        return buffer.toString();
    }

    @Override
    public String getDateConverter(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return "to_date('{" + formatter.format(date) + "}','yyyy-mm-dd hh24:mi:ss')";
    }
}
