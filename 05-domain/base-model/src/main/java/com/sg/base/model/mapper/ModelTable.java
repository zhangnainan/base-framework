package com.sg.base.model.mapper;

import com.sg.base.model.Model;

import javax.persistence.JoinColumn;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * Model-Table映射关系表。
 *
 * @author lpw
 */
public interface ModelTable {
    /**
     * 获得Model类。
     *
     * @return Model类。
     */
    Class<? extends Model> getModelClass();

    /**
     * 设置Model类。
     *
     * @param modelClass Model类。
     */
    void setModelClass(Class<? extends Model> modelClass);

    /**
     * 获得表名称。
     *
     * @return 表名称。
     */
    String getTableName();

    /**
     * 设置表名称。
     *
     * @param tableName 表名称。
     */
    void setTableName(String tableName);

    /**
     * 获得ID对应的表字段名称。
     *
     * @return ID对应的表字段名称。
     */
    String getIdColumnName();

    /**
     * 设置ID对应的表字段名称。
     *
     * @param idColumnName ID对应的表字段名称。
     */
    void setIdColumnName(String idColumnName);

    /**
     * 增加GET方法。
     *
     * @param name   方法名。
     * @param method GET方法。
     */
    void addGetMethod(String name, Method method);

    /**
     * 增加SET方法。
     *
     * @param name   方法名。
     * @param method SET方法。
     */
    void addSetMethod(String name, Method method);

    /**
     * 添加字段映射关系。
     *
     * @param columnName   字段名。
     * @param propertyName 属性名。
     */
    void addColumnAndProperties(String columnName, String propertyName);

    /**
     * 添加模型映射关系
     *
     * @param columnName 字段名称
     * @param clazz      模型类型
     */
    void addJoinColumn(String columnName, Class<? extends Model> clazz);

    /**
     * 添加join连接属性映射关系
     *
     * @param columnName   字段名称
     * @param propertyName 属性名称
     */
    void addJoinColumnName(String columnName, String propertyName);

    /**
     * 添加join连接关联字段
     *
     * @param columnName    字段名称
     * @param referenceName 关联字段
     */
    void addJoinReferenceName(String columnName, String referenceName);

    /**
     * 获取属性值。
     *
     * @param model Model实例。
     * @param name  属性名称。可以是属性名，也可以是字段名。
     * @return 属性值。如果不存在则返回null。
     */
    <T> T get(Model model, String name);

    /**
     * 获取属性值，并转化为属性所定义的类型。
     *
     * @param name
     *            属性名称。
     * @param value
     *            源属性值。
     * @return 目标属性值。
     */
    //Object get(String name, Object value);

    /**
     * 设置属性值。
     *
     * @param model Model实例。
     * @param name  属性名称。可以是属性名，也可以是字段名。
     * @param value 属性值。
     */
    void set(Model model, String name, Object value);

    /**
     * 获取字段名称集。
     *
     * @return 字段名称集。
     */
    //Set<String> getColumnNames();

    /**
     * 获取属性名称集。
     *
     * @return 属性名称集。
     */
    //Set<String> getPropertyNames();

    /**
     * 获取Jsonable配置对象。
     *
     * @param name 名称。
     * @return Jsonable配置对象；如果不存在则返回null。
     */
    //Jsonable getJsonable(String name);


    /**
     * 获取属性类型。
     *
     * @param name
     *            名称。
     * @return 属性类型；如果不存在则返回null。
     */
    //Class<?> getType(String name);

    /**
     * 复制Model属性。
     *
     * @param source    复制源。
     * @param target    目标。
     * @param containId 是否复制ID值。
     * @param <T>       Model类。
     */
    //<T extends Model> void copy(T source, T target, boolean containId);

    /**
     * 获取唯一值列的列表
     *
     * @return
     */
    List<String> getUniqueColumnList();

    /**
     * 添加唯一列
     */
    void addUniqueColumn(String columnName);

    /**
     * 添加关联获取类
     */
    //void addFetchClass(Class<? extends Model> classZ, FetchWay fetch);

    //Map<Class<? extends Model>, FetchWay> getFetchMap();

    //获取对象的关联注释
    Map<Class<? extends Model>, JoinColumn> getJoinColumn();

    /**
     * 获取以列名为关键字，字段属性为值的Map
     *
     * @return
     */
    Map<String, String> getColumns();

    /**
     * 获取以字段属性为关键字，以列表为值的Map
     *
     * @return
     */
    Map<String, String> getProperties();


    /**
     * 获取关联列表，以列名为关键字，以引用列名为值的Map
     *
     * @return
     */
    Map<String, String> getJoinReferenceColumns();
}
