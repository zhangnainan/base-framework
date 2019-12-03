/**
 *
 */
package com.sg.base.model.mapper;

import com.sg.base.bean.BeanFactory;
import com.sg.base.log.Logger;
import com.sg.base.model.Model;
import com.sg.base.model.ModelHelper;
import com.sg.base.model.annotation.Jsonable;
import com.sg.base.util.Converter;
import com.sg.base.util.Validator;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author lpw
 */
@Repository("base.model.mapper")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class ModelTableImpl implements ModelTable {
    protected Class<? extends Model> modelClass;
    protected String tableName;
    protected String idColumnName;
    protected Map<String, Method> getMethods = new ConcurrentHashMap<>();
    protected Map<String, Method> jsonableMethods = new ConcurrentHashMap<>();
    protected Map<String, Jsonable> jsonables = new ConcurrentHashMap<>();
    protected Map<String, ManyToOne> manyToOnes = new ConcurrentHashMap<>();
    protected Map<String, Method> setMethods = new ConcurrentHashMap<>();
    protected Map<String, Class<?>> types = new ConcurrentHashMap<>();
    protected Map<String, String> lowerCases = new ConcurrentHashMap<>();
    //以列名做为关键字获取字段属性的映射
    protected Map<String, String> columns = new ConcurrentHashMap<>();
    //以字段属性为关键字获取列名的映射
    protected Map<String, String> properties = new ConcurrentHashMap<>();
    protected Map<String, Class<? extends Model>> joinColumns = new ConcurrentHashMap<>();
    protected Map<String, String> joinColumnNames = new ConcurrentHashMap<>();
    protected Map<String, String> joinReferenceNames = new ConcurrentHashMap<>();
    protected List<String> uniqueColumnList = new ArrayList<>();
    //protected Map<Class<? extends Model>, FetchWay> fetchMap = new ConcurrentHashMap<>();
    protected Map<Class<? extends Model>, JoinColumn> joinColumnMap = new ConcurrentHashMap<>();

    @Override
    public Class<? extends Model> getModelClass() {
        return modelClass;
    }

    @Override
    public void setModelClass(Class<? extends Model> modelClass) {
        this.modelClass = modelClass;
    }

    @Override
    public String getTableName() {
        return tableName;
    }

    @Override
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    @Override
    public String getIdColumnName() {
        return idColumnName;
    }

    @Override
    public void setIdColumnName(String idColumnName) {
        this.idColumnName = idColumnName;
    }

    @Override
    public void addGetMethod(String name, Method method) {
        getMethods.put(name, method);
        Jsonable jsonable = method.getAnnotation(Jsonable.class);
        if (jsonable != null) {
            jsonableMethods.put(name, method);
            jsonables.put(name, jsonable);
        }
        ManyToOne manyToOne = method.getAnnotation(ManyToOne.class);
        if (manyToOne != null)
            manyToOnes.put(name, manyToOne);

        Class<?> type = method.getReturnType();
        Temporal temporal = method.getAnnotation(Temporal.class);
        if (temporal != null) {
            if (TemporalType.DATE.equals(temporal.value()))
                type = Date.class;
            else if (TemporalType.TIMESTAMP.equals(temporal.value()))
                type = Timestamp.class;
        }
        types.put(name, type);
        addLowerCase(name);
    }

    @Override
    public void addSetMethod(String name, Method method) {
        setMethods.put(name, method);
        addLowerCase(name);
    }

    @Override
    public void addColumnAndProperties(String columnName, String propertyName) {
        if (Validator.isEmpty(columnName) || Validator.isEmpty(propertyName))
            return;
        columns.put(columnName.toLowerCase(), propertyName);
        properties.put(propertyName, columnName.toLowerCase());
        addLowerCase(propertyName);
    }

    @Override
    public void addJoinColumn(String columnName, Class<? extends Model> clazz) {
        joinColumns.put(columnName.toLowerCase(), clazz);
    }

    @Override
    public void addJoinColumnName(String columnName, String propertyName) {
        joinColumnNames.put(columnName.toLowerCase(), propertyName);
        addLowerCase(propertyName);
    }

    @Override
    public void addJoinReferenceName(String columnName, String referenceName) {
        joinReferenceNames.put(columnName, referenceName);
    }

    protected void addLowerCase(String name) {
        lowerCases.put(name.substring(0, 1).toLowerCase() + name.substring(1), name);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(Model model, String name) {
        if (model == null || Validator.isEmpty(name))
            return null;

        Method method = null;
        Class<?> type = null;
        for (int i = 0; i < 3; i++) {
            String key = getKey(i, name);
            if (Validator.isEmpty(key))
                continue;

            method = getMethods.get(key);
            type = types.get(key);

            if (method != null)
                break;
        }
        if (method == null || type == null)
            return null;

        try {
            return (T) convert(type, method.invoke(model));
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            Logger.warn(e, "获取Model[{}]属性[{}]值时发生异常！", model, name);

            return null;
        }
    }

    /*@Override
    public Object get(String name, Object value) {
        if (Validator.isEmpty(name) || value == null)
            return null;

        if ("id".equalsIgnoreCase(name) || idColumnName.equalsIgnoreCase(name))
            return Converter.toString(value);

        Class<?> type = null;
        for (int i = 0; i < 3; i++) {
            String key = getKey(i, name);
            if (Validator.isEmpty(key))
                continue;

            type = types.get(key);
            if (type != null)
                break;
        }
        if (type == null)
            return null;

        return convert(type, value);
    }*/

    private <T extends Model> T createModel(Class<? extends Model> clazz) {
        return BeanFactory.getBean((Class<T>) clazz);
    }

    @Override
    public void set(Model model, String name, Object value) {
        if (model == null || Validator.isEmpty(name))
            return;

        Method method = null;
        Class<?> type = null;
        Jsonable jsonable = null;
        ManyToOne manyToOne = null;
        for (int i = 0; i < 3; i++) {
            String key = getKey(i, name);
            if (Validator.isEmpty(key))
                continue;

            method = setMethods.get(key);
            type = types.get(key);
            jsonable = jsonables.get(key);
            manyToOne = manyToOnes.get(key);

            if (method != null)
                break;
        }
        if (method == null || type == null)
            return;

        Class<? extends Model> clazz = joinColumns.get(name);
        String referenceName = joinReferenceNames.get(name);
        if (!Validator.isEmpty(clazz)) {
            Model model1 = BeanFactory.getBean(clazz);
            if (Validator.isEmpty(referenceName)) {
                model1.setId(value.toString());
            } else {
                this.set(model1, referenceName, value);
            }
            try {
                method.invoke(model, model1);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }

        try {
            if (jsonable != null)
                value = ModelHelper.format(jsonable.format(), value);
            if (manyToOne == null) {
                Object object = convert(type, value);
                if (object != null)
                    method.invoke(model, object);
            } else {
                Model m = (Model) BeanFactory.getBean(type);
                m.setId((String) value);
                method.invoke(model, m);
            }
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            Logger.warn(e, "设置Model[{}]属性[{}]值[{}]时发生异常！", model, name, value);
        }
    }

    protected String getKey(int i, String name) {
        if (i == 1)
            return lowerCases.get(name);

        if (i == 2) {
            String column = columns.get(name);
            if (Validator.isEmpty(column)) {
                return joinColumnNames.get(name);
            }
            return column;
        }

        return name;
    }

    protected Object convert(Class<?> type, Object value) {
        if (value == null || type.isInstance(value))
            return value;

        if (String.class.equals(type))
            return Converter.toString(value);

        if (int.class.equals(type) || Integer.class.equals(type))
            return Converter.toInt(value);

        if (long.class.equals(type) || Long.class.equals(type))
            return Converter.toLong(value);

        if (float.class.equals(type) || Float.class.equals(type))
            return Converter.toFloat(value);

        if (double.class.equals(type) || Double.class.equals(type))
            return Converter.toDouble(value);

        if (Date.class.equals(type) || Timestamp.class.equals(type)) {
            java.util.Date date = Converter.toDate(value);
            if (date == null)
                return null;

            if (Date.class.equals(type))
                return new Date(date.getTime());

            return new Timestamp(date.getTime());
        }

        return null;
    }

    /*@Override
    public Set<String> getColumnNames() {
        return columns.keySet();
    }

    @Override
    public Set<String> getPropertyNames() {
        return getMethods.keySet();
    }*/

    /*@Override
    public Jsonable getJsonable(String name) {
        for (int i = 0; i < 3; i++) {
            String key = getKey(i, name);
            if (Validator.isEmpty(key))
                continue;

            Jsonable jsonable = jsonables.get(key);
            if (jsonable != null)
                return jsonable;
        }

        return null;
    }*/

    /*@Override
    public Class<?> getType(String name) {
        for (int i = 0; i < 3; i++) {
            String key = getKey(i, name);
            if (Validator.isEmpty(key))
                continue;

            Class<?> type = types.get(key);
            if (type != null)
                return type;
        }

        return null;
    }*/

    /*@Override
    public <T extends Model> void copy(T source, T target, boolean containId) {
        if (containId)
            target.setId(source.getId());
        setMethods.forEach((name, set) -> {
            Method get = getMethods.get(name);
            if (get == null) {
                Logger.warn(null, "无法获得Get[{}]方法！", name);

                return;
            }

            try {
                set.invoke(target, get.invoke(source));
            } catch (IllegalAccessException | InvocationTargetException e) {
                Logger.warn(e, "复制Model属性时发生异常！");
            }
        });
    }*/

    @Override
    public List<String> getUniqueColumnList() {
        return uniqueColumnList;
    }

    @Override
    public void addUniqueColumn(String columnName) {
        if (!Validator.isEmpty(columnName))
            uniqueColumnList.add(columnName);
    }

    /*@Override
    public void addFetchClass(Class<? extends Model> classZ, FetchWay fetch) {
        if (Validator.isEmpty(classZ))
            return;
        if (Validator.isEmpty(fetch))
            return;
        fetchMap.put(classZ, fetch);
    }

    @Override
    public Map<Class<? extends Model>, FetchWay> getFetchMap() {
        return fetchMap;
    }*/

    @Override
    public Map<Class<? extends Model>, JoinColumn> getJoinColumn() {
        return joinColumnMap;
    }

    @Override
    public Map<String, String> getColumns() {
        return this.columns;
    }

    @Override
    public Map<String, String> getProperties() {
        return this.properties;
    }


    @Override
    public Map<String, String> getJoinReferenceColumns() {
        return this.joinReferenceNames;
    }
}
