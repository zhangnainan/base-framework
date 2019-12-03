package com.sg.base.crud.service;

import com.sg.base.bean.BeanFactory;
import com.sg.base.conf.CrudConfiguration;
import com.sg.base.crud.CrudService;
import com.sg.base.crud.enums.DeleteType;
import com.sg.base.crud.exception.ExistsException;
import com.sg.base.crud.exception.ExistsInRecycleBinException;
import com.sg.base.crud.service.proxy.*;
import com.sg.base.dao.orm.OrmContext;
import com.sg.base.dao.orm.Query;
import com.sg.base.log.Logger;
import com.sg.base.model.Model;
import com.sg.base.model.PageList;
import com.sg.base.model.annotation.Datasource;
import com.sg.base.model.enums.Criterion;
import com.sg.base.model.enums.Global;
import com.sg.base.model.enums.Operator;
import com.sg.base.model.mapper.ModelTables;
import com.sg.base.model.support.BaseModel;
import com.sg.base.model.support.BaseModelSupport;
import com.sg.base.model.support.user.BaseModelHelper;
import com.sg.base.model.support.user.UserHelper;
import com.sg.base.util.Converter;
import com.sg.base.util.Validator;
import net.sf.json.JSONArray;
import org.apache.commons.lang.NotImplementedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.Transient;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 封装了一些通用的增删改查接口 同时对通用查询增加底层字段的过滤
 *
 * @author Dai Wenqing
 * @date 2015/11/6
 */
@Component("base.crud.service.basic")
public class CrudServiceBasic implements CrudService, QueryService, ExecuteService {
    //protected CrudDao crudDao;
    @Autowired
    protected OrmManage ormManage;
    @Autowired(required = false)
    protected BaseModelHelper baseModelHelper;
    @Autowired
    protected CrudConfiguration crudConfiguration;
    @Autowired(required = false)
    protected UserHelper userHelper;
    @Autowired
    protected ModelTables modelTables;

    protected <T extends Model> boolean doMustNotNull(Class<T> classZ) {
        if (classZ == null) {
            Logger.error(new NullPointerException("ModelClass must not null"), "");
            return true;
        }
        return false;
    }

    /*
     * public <T extends Model> T exists(Class<T> classZ, String key, Object
     * value, boolean excludeDomain) { doMustNotNull(classZ); if (key == null ||
     * value == null) return null;
     * 
     * Map<String, Criterion> criterionMap = new LinkedHashMap<>();
     * criterionMap.put(key, Criterion.Equals); List<Object> args = new
     * ArrayList<>(); args.add(value); PageList<T> pageList = query(classZ,
     * criterionMap, args, -1, -1, excludeDomain, 1); if
     * (pageList.getList().size() > 0) return pageList.getList().get(0); return
     * null; }
     * 
     * public <T extends Model> T exists(Class<T> classZ, Map<String, Criterion>
     * criterionMap, List<Object> args, boolean excludeDomain) { PageList<T>
     * pageList = query(classZ, criterionMap, args, -1, -1, excludeDomain, 1);
     * if (pageList.getList().size() > 0) return pageList.getList().get(0);
     * return null; }
     */

    protected List<Object> argsToList(Object... args) {
        List<Object> list = new ArrayList<>();

        for (int i = 0; i < args.length; i++) {

            if (args[i] instanceof Collection<?>) {
                List<Object> l = (List<Object>) args[i];
                for (Object o : l) {
                    list.add(o);
                }
            } else {
                list.add(args[i]);
            }
        }
        return list;
    }

    protected Query setDomain(Query query, boolean excludeDomain) {
        if (query == null)
            return null;
        // 测定实现实现了BaseModel
        OrmContext ormContext = OrmContext.class.cast(query);
        Model model = null;
        try {
            model = BeanFactory.getBean(ormContext.getFrom());
        } catch (Exception ex) {
            Logger.debug("未指定类型的查询");
            return query;
        }
        if (!Validator.isEmpty(model)) {
            if (model instanceof BaseModel) {
                // ormContext.getQueryContext().put("t0.validFlag", 1);
                if (!excludeDomain) {
                    if (userHelper != null) {
                        // query.where("t0.domain", Criterion.Equals,
                        // userHelper.getDomain());
                        ormContext.getQueryContext().put("t0.domain", userHelper.getDomain());
                    } else {
                        throw new NotImplementedException("no implement the userHelper interface,please check again");
                    }
                }
            }
        }
        return query;
    }

    /**
     * 设置有效无效数据
     *
     * @param query
     * @param queryFlag 查询标记类型 （-1 = 只查询无效的数据,0=只查询有效数据,1=查询全部数据）
     * @return
     */
    protected Query setValidFlag(Query query, Global.QueryFlag queryFlag) {
        if (query == null)
            return null;
        // 测定实现实现了BaseModel
        OrmContext ormContext = OrmContext.class.cast(query);
        Model model = null;
        try {
            model = BeanFactory.getBean(ormContext.getFrom());
        } catch (Exception ex) {
            Logger.debug("未指定类型的查询");
            return query;
        }
        if (!Validator.isEmpty(model)) {
            if (model instanceof BaseModel) {
                if (queryFlag.getType() < 1) {
                    // criterionMap.put("validFlag", Criterion.Equals);
                    if (queryFlag == Global.QueryFlag.Valid)
                        ormContext.getQueryContext().put("t0.validFlag", Global.ValidFlag.Valid.getType());
                    else {
                        ormContext.getQueryContext().put("t0.validFlag", Global.ValidFlag.Delete.getType());
                    }
                }
            }
        }
        return query;
    }

    protected Query setQueryCondition(Query query, boolean excludeDomain, Global.QueryFlag queryFlag) {
        setDomain(query, excludeDomain);
        // 获取有效的数据
        setValidFlag(query, queryFlag);
        return query;
    }

    @Override
    public QueryProxy query(String... name) {
        if (name.length > 0)
            return new QueryProxy(ormManage.getQuery(name[0]), this);
        return new QueryProxy(ormManage.getQuery(), this);
    }

    @Override
    public ExecuteProxy execute() {
        return new ExecuteProxy(ormManage.getQuery(), this);
    }

    @Override
    public SqlProxy sql(String sql) {
        return new SqlProxy(sql);
    }

    @Override
    public SqlProxy sql(String tableName, String sql) {
        return new SqlProxy(tableName, sql);
    }

    @Override
    public SqlProxy sql(Class<? extends Model> classZ, String sql) {
        return new SqlProxy(classZ, sql);
    }


    @Override
    public MapProxy map(Class<? extends Model> classZ) {
        return new MapProxy(classZ, this);
    }

    private void setArgs(Query query, Object... args) {
        List<Object> argList = argsToList(args);
        if (!Validator.isEmpty(args)) {
            int ndx = 0;
            OrmContext ormContext = OrmContext.class.cast(query);
            for (Object o : args) {
                ormContext.getArgs().add(ndx, o);
                ndx++;
            }
        }
    }

    private boolean setDatasource(Query query) {
        OrmContext ormContext = OrmContext.class.cast(query);
        if (doMustNotNull(ormContext.getFrom()))
            return false;
        String ds = "";
        Datasource datasource = ormContext.getFrom().getAnnotation(Datasource.class);
        if (!Validator.isEmpty(datasource))
            ds = datasource.value();
        query.datasource(ds);
        return true;
    }

    private <T extends Model> String getDatasource(Class<T> modelClass) {
        Datasource datasource = modelClass.getAnnotation(Datasource.class);
        if (Validator.isEmpty(datasource))
            return "";
        return datasource.value();
    }


    @Override
    public <T extends Model> T one(Query query, boolean excludeDomain, Object... args) {
        //doMustNotNull(query
        setArgs(query, args);
        if (!setDatasource(query))
            return null;
        return ormManage.getOrm().findOne(setQueryCondition(query, excludeDomain, Global.QueryFlag.Valid));
    }

    @Override
    public <T extends Model> PageList<T> list(Query query, boolean excludeDomain, Object... args) {
        setArgs(query, args);
        if (!setDatasource(query))
            return BeanFactory.getBean(PageList.class);
        return ormManage.getOrm().query(setQueryCondition(query, excludeDomain, Global.QueryFlag.Valid));
    }

    @Override
    public <T extends Model> PageList<T> all(Query query, boolean excludeDomain, Object... args) {
        setArgs(query, args);
        if (!setDatasource(query))
            return BeanFactory.getBean(PageList.class);
        return ormManage.getOrm().query(setQueryCondition(query, excludeDomain, Global.QueryFlag.All));
    }

    @Override
    public int count(Query query, boolean excludeDomain, Object... args) {
        setArgs(query, args);
        if (!setDatasource(query))
            return 0;
        return ormManage.getOrm().count(setQueryCondition(query, excludeDomain, Global.QueryFlag.Valid));
    }

    @Override
    public JSONArray asJson(Query query, String ormName) {
        if (Validator.isEmpty(ormName) || Validator.isEmpty(query))
            return new JSONArray();
        return ormManage.getOrm(ormName).getAsJson(query);
    }

    /*
     * public <T extends Model> boolean exists(Class<T> classZ, String key,
     * Object value) { return exists(classZ, key, value, false) != null; }
     */

    @SuppressWarnings({"unchecked"})
    @Override
    public <T extends Model> T findById(Class<T> classZ, String id) {
        doMustNotNull(classZ);

        if (id == null)
            return null;
        return ormManage.getOrm().findById(classZ, id);
    }

    @Override
    public <T extends Model> boolean deleteById(Class<T> classZ, String id) {
        //return de;
        Query query = ormManage.getQuery();
        query.from(classZ);
        query.where("id", id);
        return deleteOrRecycle(query, DeleteType.Delete);
    }

    @Override
    public <T extends Model> Boolean save(T model) throws ExistsException, ExistsInRecycleBinException {
        if (Validator.isEmpty(model))
            return false;
        if (baseModelHelper != null)
            baseModelHelper.initBaseModel((BaseModel) model);

        hasExist(model);

        return ormManage.getOrm().save(model, getDatasource(model.getClass()));
    }

    /**
     * 重复判断
     *
     * @param model
     * @param <T>
     */
    private <T extends Model> void hasExist(T model) {
        List<String> uniqueList = modelTables.get(model.getClass()).getUniqueColumnList();
        //只是一个计算器而已
        List<Object> args = new ArrayList<>();
        // Map<String, Criterion> map = new LinkedHashMap<>();
        QueryProxy queryProxy = query().from(model.getClass());
        uniqueList.forEach(c -> {
            Object object = null;
            try {
                object = queryProxy.where(c, model.getClass().getMethod("get" + Converter.toFirstUpperCase(c)).invoke(model), Operator.Or);
            } catch (Exception ex) {
                Logger.error(ex, ex.getMessage());
            }
            //不发生异常 object 一定不为空
            if (!Validator.isEmpty(object))
                args.add(object);

        });
        PageList<T> pageList = BeanFactory.getBean(PageList.class);
        if (args.size() > 0) {
            pageList = queryProxy.list();
        }
        if (pageList.getList().size() > 0) {
            // 如果是修改，当前修改的实体的ID不能与库里有ID相等
            if ((!Validator.isEmpty(model.getId()) && !pageList.getList().get(0).getId().equals(model.getId()))
                    || Validator.isEmpty(model.getId())) {
                BaseModelSupport baseModelSupport = BaseModelSupport.class.cast(pageList.getList().get(0));
                //model.setId(baseModelSupport.getId());
                int valid = baseModelSupport.getValidFlag();
                if (valid == 1)
                    throw new ExistsException(uniqueList.toArray());

                else if (valid == 0)
                    throw new ExistsInRecycleBinException(uniqueList.toArray());
            }
        }
    }

    /**
     * 批量保存
     * <p>
     * 资源定位地址
     *
     * @param modelList 对象列表
     * @return
     */
    @Override
    public <T extends Model> Boolean saveList(List<T> modelList) {
        if (modelList == null)
            return false;

        boolean result = true;
        for (Model model : modelList)
            result = result && save(model);
        return result;
    }

    @Override
    public <T extends Model> Boolean update(T model) {
        return update(model, false);
    }

    @Override
    public <T extends Model> Boolean update(T model, boolean toValidator) {
        if (Validator.isEmpty(model))
            return false;
        if (toValidator)
            hasExist(model);
        Query query = ormManage.getQuery();
        for (Method method : model.getClass().getMethods()) {
            if (BeanFactory.getBean(method.getReturnType()) instanceof Model)
                continue;
            if (!method.getName().startsWith("get"))
                continue;
            OrmContext ormContext = OrmContext.class.cast(query);
            query.from(model.getClass());
            setDatasource(query);
            try {
                Object value = method.invoke(model);
                if (Validator.isEmpty(value))
                    continue;
                else if (method.getAnnotation(Transient.class) != null)
                    continue;
                else if (value instanceof Integer) {
                    if (value.toString().equals(Integer.MIN_VALUE + ""))
                        continue;
                } else if (value instanceof Double) {
                    if (value.toString().equals(Double.MIN_VALUE + ""))
                        continue;
                } else if (value instanceof Float) {
                    if (value.toString().equals(Float.MIN_VALUE + ""))
                        continue;
                } else if (value instanceof Long) {
                    if (value.toString().equals(Long.MIN_VALUE + ""))
                        continue;
                }
                String name = Converter.toFirstLowerCase(method.getName().substring("get".length()));
                if (name.toLowerCase().equals("class"))
                    continue;
                if (name.toLowerCase().equals("id"))
                    query.where(name, method.invoke(model));
                else
                    query.set(name, method.invoke(model));
            } catch (Exception e) {
                Logger.error(e, "更新操作，set 获取取值时出错");
            }
        }
        return ormManage.getOrm().update(query);
    }

    @Override
    public <T extends Model> boolean deleteOrRecycle(Query query, DeleteType deleteType) {
        if (query == null)
            return false;
        PageList<T> modelList = all(query, false);
        modelList.getList().forEach(model -> {
            deleteOrRecycle(deleteType, model);
        });
        return true;
    }

    @Override
    public boolean update(Query query, Object... args) {
        setArgs(query, args);
        if (!setDatasource(query))
            return false;
        return ormManage.getOrm().update(query);
    }

    private void deleteOrRecycle(DeleteType deleteType, Model model) {
        if (model != null) {
            switch (deleteType) {
                case Recycle:
                    // criterionMap.put("validFlag", Criterion.Equals);
                    ((BaseModel) model).setValidFlag(DeleteType.Recycle.getType());
                    // crudDao.updateById(metaData.getDataSource(),
                    // metaData.getModelClass(), model.getId());
                    break;
                case Remove:
                    ((BaseModel) model).setValidFlag(DeleteType.Remove.getType());
                    break;
                case Delete:
                    ((BaseModel) model).setValidFlag(DeleteType.Delete.getType());
                    break;
            }
            ormManage.getOrm().save(model, getDatasource(model.getClass()));
        }
    }

    /*@Override
    public <T extends Model> Boolean clear(Class<T> classZ, Map<String, Criterion> criterionMap, Object[] args) {
        doMustNotNull(classZ);
        if (classZ == null)
            return false;
        crudDao.remove(classZ, criterionMap, args);
        return true;
    }*/

    @Override
    public Query mapToQuery(Class<? extends Model> classZ, Map<String, Criterion> criterionMap, int page, int size, Object... args) {
        Query query = ormManage.getQuery();
        if (criterionMap == null)
            return query;
        List<Object> argList = argsToList(args);
        query.from(classZ);
        query.paging(page, size);
        /*
         * OrmContext ormContext = OrmContext.class.cast(query);
         * 
         * // 测定实现实现了BaseModel Model model = BeanFactory.getBean(classZ); if
         * (!Validator.isEmpty(model)) { if (model instanceof BaseModel) { if
         * (validType < 1) { if (criterionMap.get("validFlag") == null) { //
         * criterionMap.put("validFlag", Criterion.Equals); if (validType == 0)
         * ormContext.getQueryContext().put("t0.validFlag", 1); else {
         * ormContext.getQueryContext().put("t0.validFlag", 0); } } } }
         * 
         * if (!excludeDomain) { if (userHelper != null) {
         * ormContext.getQueryContext().put("t0.domain",
         * userHelper.getDomain()); } else { throw new NotImplementedException(
         * "no implement the userHelper interface,please check again"); } } }
         */

        int ndx = 0;
        for (String key : criterionMap.keySet()) {
            if (criterionMap.get(key) == Criterion.Between) {
                query.where(key, criterionMap.get(key), new Object[]{argList.get(ndx), argList.get(++ndx)});
            } else {
                query.where(key, criterionMap.get(key), argList.get(ndx));
            }
            ndx++;
        }
        //setArgs(query, args);
        return query;
    }
}
