package com.sg.base.dao.sql;

import com.sg.base.dao.Mode;
import com.sg.base.log.Logger;
import com.sg.base.util.Converter;
import com.sg.base.util.Validator;
import net.sf.json.JSONArray;
import org.springframework.stereotype.Repository;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

/**
 * @author lpw
 */
@Repository("base.dao.sql.procedure")
public class ProcedureImpl extends JdbcSupport<CallableStatement> implements Procedure {
    @Override
    public SqlTable query(String sql, int size, int page, Object[] args) {


        try {
            if (size > 0)
                sql = connectionManage.getDialect().appendPagination(sql, size, page);
            CallableStatement pstmt = newPreparedStatement(Mode.Read, sql);
            setArgs(pstmt, args);
            int index = (Validator.isEmpty(args) ? 0 : args.length) + 1;
            pstmt.registerOutParameter(index, Types.REF_CURSOR);
            pstmt.execute();
            SqlTable sqlTable = query((ResultSet) pstmt.getObject(index));
            pstmt.close();

            if (Logger.isDebugEnable())
                Logger.debug("执行SQL[{}:{}]检索操作。", sql, Converter.toString(args));

            return sqlTable;
        } catch (SQLException e) {
            Logger.warn(e, "执行SQL[{}:{}]检索时发生异常！", sql, Converter.toString(args));

            throw new RuntimeException(e);
        }
    }

    @Override
    public JSONArray queryAsJson(String sql, int size, int page, Object[] args) {


        try {
            if (size > 0)
                sql = connectionManage.getDialect().appendPagination(sql, size, page);
            CallableStatement pstmt = newPreparedStatement(Mode.Read, sql);
            setArgs(pstmt, args);
            int index = (Validator.isEmpty(args) ? 0 : args.length) + 1;
            pstmt.registerOutParameter(index, Types.REF_CURSOR);
            pstmt.execute();
            JSONArray array = queryAsJson((ResultSet) pstmt.getObject(index));
            pstmt.close();

            if (Logger.isDebugEnable())
                Logger.debug("执行SQL[{}:{}]检索操作。", sql, Converter.toString(args));

            return array;
        } catch (SQLException e) {
            Logger.warn(e, "执行SQL[{}:{}]检索时发生异常！", sql, Converter.toString(args));

            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings({"unchecked"})
    @Override
    public <T> T queryObject(String sql, Object[] args) {
        if (Logger.isDebugEnable())
            Logger.debug("执行SQL[{}:{}]检索操作。", sql, Converter.toString(args));

        try {
            CallableStatement pstmt = newPreparedStatement(Mode.Read, sql);
            setArgs(pstmt, args);
            int index = (Validator.isEmpty(args) ? 0 : args.length) + 1;
            pstmt.registerOutParameter(index, Types.JAVA_OBJECT);
            pstmt.execute();
            T object = (T) pstmt.getObject(index);
            pstmt.close();

            return object;
        } catch (SQLException e) {
            Logger.warn(e, "执行SQL[{}:{}]检索时发生异常！", sql, Converter.toString(args));

            throw new RuntimeException(e);
        }
    }

    protected CallableStatement newPreparedStatement(Mode mode, String sql) throws SQLException {
        return getConnection(mode).prepareCall(sql);
    }
}
