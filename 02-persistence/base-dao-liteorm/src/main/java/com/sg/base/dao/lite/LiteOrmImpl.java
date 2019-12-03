/**
 *
 */
package com.sg.base.dao.lite;

import com.sg.base.bean.BeanFactory;
import com.sg.base.dao.sql.Sql;
import com.sg.base.dao.sql.SqlTable;
import com.sg.base.log.Logger;
import com.sg.base.model.Model;
import com.sg.base.model.ModelHelper;
import com.sg.base.model.PageList;
import com.sg.base.model.mapper.ModelTable;
import com.sg.base.model.mapper.ModelTables;
import com.sg.base.model.support.BaseModel;
import com.sg.base.util.Converter;
import com.sg.base.util.Generator;
import com.sg.base.util.Validator;
import net.sf.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

/**
 * @author lpw
 */
@Repository("base.dao.orm.lite")
public class LiteOrmImpl implements LiteOrm {

    @Autowired(required = false)
    protected ModelHelper modelHelper;

    @Autowired
    protected Sql sql;
    @Autowired
    private ModelTables modelTables;

    @Override
    public String getOrmName() {
        return "lite";
    }


    @Override
    public <T extends Model> T findById(Class<T> modelClass, String id, String... datasource) {
        if (Validator.isEmpty(id))
            return null;
        LiteQuery liteQuery = new LiteQuery();
        liteQuery.from(modelClass).where("id =?");
        return queryOne(liteQuery);
    }

    @Override
    public <T extends Model> T findOne(LiteQuery query) {
        query.paging(1, 1);
        return queryOne(query);
    }

    @SuppressWarnings("unchecked")
    protected <T extends Model> T queryOne(LiteQuery query) {
        List<T> list = (List<T>) query(query, false).getList();

        return Validator.isEmpty(list) ? null : list.get(0);
    }

    @Override
    public <T extends Model> PageList<T> query(LiteQuery query) {
        return query(query);
    }

    @SuppressWarnings("unchecked")
    protected <T extends Model> PageList<T> query(LiteQuery query, boolean countable) {
        PageList<T> models = BeanFactory.getBean(PageList.class);
        if (countable && query.getSize() > 0)
            models.setPage(count(query), query.getSize(), query.getPage());
        models.setList(new ArrayList<>());

        ModelTable modelTable = modelTables.get(query.getFrom());
        String querySql = getQuerySql(query);

        SqlTable sqlTable = sql.query(querySql, query.getSize(), query.getPage(), new Object[]{});
        if (sqlTable.getRowCount() == 0)
            return models;

        String[] columnNames = sqlTable.getNames();
        for (int i = 0; i < sqlTable.getRowCount(); i++) {
            T model = BeanFactory.getBean((Class<T>) query.getFrom());
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

    protected String getQuerySql(LiteQuery query) {
        StringBuilder querySql = new StringBuilder().append("SELECT ").append(Validator.isEmpty(query.getSelect()) ? "*" : query.getSelect());

        querySql.append(" FROM ").append(Validator.isEmpty(query.getFrom()) ? query.getFrom().getAnnotation(Table.class).name() : query.getFrom());
        if (!Validator.isEmpty(query.getWhere()))
            querySql.append(" WHERE ").append(query.getWhere());
        if (!Validator.isEmpty(query.getGroup()))
            querySql.append(" GROUP BY ").append(query.getGroup());
        if (!Validator.isEmpty(query.getOrder()))
            querySql.append(" ORDER BY ").append(query.getOrder());

        return querySql.toString();
    }

    @Override
    public int count(LiteQuery query) {
        StringBuilder querySql = new StringBuilder().append("SELECT COUNT(*)");
        querySql.append(" FROM ").append(Validator.isEmpty(query.getFrom()) ? query.getFrom().getAnnotation(Table.class).name() : query.getFrom());
        if (!Validator.isEmpty(query.getWhere()))
            querySql.append(" WHERE ").append(query.getWhere());
        if (!Validator.isEmpty(query.getGroup()))
            querySql.append(" GROUP BY ").append(query.getGroup());

        SqlTable sqlTable = sql.query(querySql.toString(), query.getSize(), query.getPage(), new Object[]{});
        if (sqlTable.getRowCount() == 0)
            return 0;

        return Converter.toInt(sqlTable.get(0, 0));
    }


    @Override
    public <T extends Model> boolean save(T model, String... datasource) {
        if (model == null) {
            Logger.warn(null, "要保存的Model为null！");

            return false;
        }

        if (Validator.isEmpty(model.getId()))
            model.setId(null);

        // ModelTable modelTable = modelTables.get(model.getClass());
        if (model.getId() == null)
            return insert(model);

        return update(model, modelTables.get(model.getClass())) == 1;
    }

    /**
     * 新增。
     *
     * @param model Model实例。
     * @return 影响记录数。
     */
    @Override
    public <T extends Model> boolean insert(T model) {
        ModelTable modelTable = modelTables.get(model.getClass());
        StringBuilder insertSql = new StringBuilder().append("INSERT INTO ").append(modelTable.getTableName()).append('(');
        List<Object> args = new ArrayList<>();
        if (model.getId() == null && model instanceof BaseModel)
            model.setId(Generator.uuid().replaceAll("-", ""));
        int columnCount = 0;
        if (model.getId() != null) {
            insertSql.append(modelTable.getIdColumnName());
            args.add(model.getId());
            columnCount++;
        }
        for (String columnName : modelTable.getColumns().keySet()) {
            if (columnCount > 0)
                insertSql.append(',');
            insertSql.append(columnName);
            args.add(modelTable.get(model, columnName));
            columnCount++;
        }
        insertSql.append(") VALUES(");
        for (int i = 0; i < columnCount; i++) {
            if (i > 0)
                insertSql.append(',');
            insertSql.append('?');
        }
        insertSql.append(")");

        int n = sql.update(insertSql.toString(), args.toArray());

        if (n == 0)
            Logger.warn(null, "新增操作失败！sql:{};args:{}", insertSql, ModelHelper.toJson(model));

        return n == 1;
    }

    /**
     * 更新。
     *
     * @param model      Model实例。
     * @param modelTable Model-Table映射关系表。
     * @return 影响记录数。
     */
    protected <T extends Model> int update(T model, ModelTable modelTable) {
        StringBuilder updateSql = new StringBuilder().append("UPDATE ").append(modelTable.getTableName()).append(" SET ");
        List<Object> args = new ArrayList<>();
        for (String columnName : modelTable.getColumns().keySet()) {
            if (!args.isEmpty())
                updateSql.append(',');
            updateSql.append(columnName).append("=?");
            args.add(modelTable.get(model, columnName));
        }
        updateSql.append(" WHERE ").append(modelTable.getIdColumnName()).append("=?");
        args.add(model.getId());

        int n = sql.update(updateSql.toString(), args.toArray());

        if (n == 0)
            Logger.warn(null, "更新操作失败！sql:{};args:{}", updateSql, modelHelper.toJson(model));

        return n;
    }

    @Override
    public boolean update(LiteQuery query) {
        ModelTable modelTable = modelTables.get(query.getFrom());
        StringBuilder updateSql = new StringBuilder().append("UPDATE ").append(modelTable.getTableName()).append(" SET ").append(query.getSet());
        if (!Validator.isEmpty(query.getWhere()))
            updateSql.append(" WHERE ").append(query.getWhere()).append(';');

        int n = sql.update(updateSql.toString(), new Object[]{});

        if (n == 0)
            Logger.warn(null, "更新操作失败！sql:{};args:{}", updateSql, "");

        return n > 0;
    }

    @Override
    public <T extends Model> boolean delete(T model, String... datasource) {
        return false;
    }
    /*
     * @Override public <T extends Model> boolean delete(String dataSource, T
     * model) { if (model == null) return false;
     * 
     * if (Validator.isEmpty(model.getId())) return false;
     * 
     * return delete(new
     * LiteQuery(model.getClass()).dataSource(dataSource).where(modelTables.get(
     * model.getClass()).getIdColumnName() + "=?"), new Object[] { model.getId()
     * }); }
     */

    @Override
    public boolean delete(LiteQuery query) {
        ModelTable modelTable = modelTables.get(query.getFrom());
        StringBuilder deleteSql = new StringBuilder().append("DELETE FROM ").append(modelTable.getTableName());
        if (!Validator.isEmpty(query.getWhere()))
            deleteSql.append(" WHERE ").append(query.getWhere());

        int n = sql.update(deleteSql.toString(), new Object[]{});

        if (n == 0)
            Logger.warn(null, "删除操作失败！sql:{};args:{}", deleteSql, "");

        return n > 0;
    }

    @Override
    public JSONArray getAsJson(LiteQuery query) {
        return null;
    }
}
