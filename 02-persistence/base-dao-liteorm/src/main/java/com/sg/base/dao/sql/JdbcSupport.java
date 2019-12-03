package com.sg.base.dao.sql;

import com.sg.base.bean.BeanFactory;
import com.sg.base.dao.ConnectionManage;
import com.sg.base.dao.Mode;
import com.sg.base.log.Logger;
import com.sg.base.model.Model;
import com.sg.base.model.PageList;
import com.sg.base.model.mapper.ModelTable;
import com.sg.base.model.mapper.ModelTables;
import com.sg.base.util.Converter;
import com.sg.base.util.Validator;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author lpw
 */
public abstract class JdbcSupport<T extends PreparedStatement> implements Jdbc {
    protected Map<String, String> labels = new ConcurrentHashMap<>();
    @Autowired
    protected ConnectionManage<Connection> connectionManage;
    @Autowired
    protected ModelTables modelTables;

    protected <T extends Model> PageList<T> sqlTableToPageList(Class<? extends Model> classZ, SqlTable sqlTable, int count, int size, int number) {
        PageList<T> models = BeanFactory.getBean(PageList.class);
        models.setPage(count, size, number);
        if (Validator.isEmpty(classZ) || Validator.isEmpty(sqlTable))
            return models;
        ModelTable modelTable = modelTables.get(classZ);
        String[] columnNames = sqlTable.getNames();
        for (int i = 0; i < sqlTable.getRowCount(); i++) {
            T model = BeanFactory.getBean((Class<T>) classZ);
            for (int j = 0; j < columnNames.length; j++) {
                if (columnNames[j].equals(modelTable.getIdColumnName()))
                    model.setId(sqlTable.get(i, j));
                else
                    modelTable.set(model, columnNames[j], sqlTable.get(i, j));
            }
            models.getList().add(model);
        }
        return models;
    }

    protected SqlTable query(ResultSet rs) throws SQLException {
        SqlTable sqlTable = BeanFactory.getBean(SqlTable.class);
        sqlTable.set(rs);
        rs.close();

        return sqlTable;
    }

    protected JSONArray queryAsJson(ResultSet rs) throws SQLException {
        JSONArray array = new JSONArray();
        List<String> names = new ArrayList<>();
        int column = rs.getMetaData().getColumnCount();
        for (int i = 1; i <= column; i++)
            names.add(rs.getMetaData().getColumnLabel(i));
        for (; rs.next(); ) {
            JSONObject object = new JSONObject();
            for (String name : names)
                object.put(formatColumnLabel(name), Converter.toString(rs.getObject(name)));
            array.add(object);
        }
        rs.close();

        return array;
    }

    protected String formatColumnLabel(String label) {
        if (Validator.isEmpty(label))
            return label;

        String string = labels.get(label);
        if (string != null)
            return string;

        StringBuffer sb = new StringBuffer();
        boolean line = false;
        for (char ch : label.toLowerCase().toCharArray()) {
            if (ch == '_') {
                line = true;

                continue;
            }

            if (line) {
                line = false;
                sb.append((char) (ch >= 'a' && ch <= 'z' ? (ch - 'a' + 'A') : ch));

                continue;
            }

            sb.append(ch);
        }
        string = sb.toString();
        labels.put(label, string);

        return string;
    }

    @Override
    public int update(String sql, Object[] args) {
        if (Logger.isDebugEnable())
            Logger.debug("成功执行SQL[{}:{}]更新操作。", sql, Converter.toString(args));

        try {
            T pstmt = newPreparedStatement(Mode.Write, sql);
            setArgs(pstmt, args);
            int n = pstmt.executeUpdate();
            pstmt.close();

            return n;
        } catch (SQLException e) {
            Logger.warn(e, "执行SQL[{}:{}]更新时发生异常！", sql, Converter.toString(args));

            throw new RuntimeException(e);
        }
    }

    protected abstract T newPreparedStatement(Mode mode, String sql) throws SQLException;

    protected Connection getConnection(Mode mode) throws SQLException {
        Connection connection = connectionManage.get(mode);
        if (connection == null)
            throw new NullPointerException("无法获得数据库[" + mode + "]连接！");

        return connection;
    }

    /**
     * 设置参数集。
     *
     * @param pstmt PreparedStatement实例。
     * @param args  参数集。
     * @throws SQLException
     */
    protected void setArgs(T pstmt, Object[] args) throws SQLException {
        if (Validator.isEmpty(args))
            return;

        for (int i = 0; i < args.length; i++)
            pstmt.setObject(i + 1, args[i]);
    }

}
