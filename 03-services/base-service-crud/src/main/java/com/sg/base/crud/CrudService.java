package com.sg.base.crud;

import com.sg.base.crud.exception.ExistsException;
import com.sg.base.crud.exception.ExistsInRecycleBinException;
import com.sg.base.crud.service.proxy.*;
import com.sg.base.model.Model;

import java.util.List;

/**
 * 提供多样灵活查询方式，即有最简单的根据主键的查询方式例如：
 * findById(),save(),saveList()
 *
 * @author Dai Wenqing
 * @date 2015/11/6
 */
public interface CrudService {

    /**
     * 查询代理，允许自定义查询，满足复杂查询需求
     *
     * @param ormName
     * @return
     */
    QueryProxy query(String... ormName);

    /**
     * 可以执行代理，用的于删除、收回、清除数据
     *
     * @return
     */
    ExecuteProxy execute();

    /**
     * 自定义SQL代理
     *
     * @param sql
     * @return
     */
    SqlProxy sql(String sql);

    SqlProxy sql(String tableName, String sql);

    SqlProxy sql(Class<? extends Model> classZ, String sql);

    //SqlProxy sql(Class<? extends Model> classZ, SqlQueryProxy)

    /**
     * 构建简单的条件查询，所有条件全部为"=="，条件之间全部为“and”关系
     *
     * @param classZ 映射转换的结果类型
     * @return
     */
    MapProxy map(Class<? extends Model> classZ);


    /**
     * 获取指定ID的相关实体
     * 此方法不管数据有效无效均能查询出来
     *
     * @param id  数据ID值。
     * @param <T> Model类。
     * @return 指定ID的数据；如果不存在则返回null。
     */
    <T extends Model> T findById(Class<T> classZ, String id);

    /**
     * 根据条件进行删除
     *
     * @param classZ 类型
     * @param id     主键
     * @param <T>
     * @return
     */
    <T extends Model> boolean deleteById(Class<T> classZ, String id);

    /**
     * 保存数据，会做数据重复性检查，只需在需要判断重复性字上增加注解：@Unique
     * 属于重量级操作
     * <p>
     * 1、实体ID为空时为新增行数据；
     * 2、实休ID不为空时为更新行数据，非ID字段的值为空时，会将其空值保存到数据库(更新操作慎用)
     *
     * @param model 实体对象
     * @return
     */
    <T extends Model> Boolean save(T model) throws ExistsException, ExistsInRecycleBinException;

    /**
     * 批量保存
     *
     * @param modelList 对象列表
     * @return
     */
    <T extends Model> Boolean saveList(List<T> modelList);

    /**
     * 更新操作，允许根据主键ID，进行部分字段更新，但是不会做重复判断
     * 属于轻量级操作，满足高效的更新
     * 只能做单表操作
     *
     * @param model 要保存的实体
     * @param <T>   实体类型
     * @return
     */
    <T extends Model> Boolean update(T model);

    /**
     * 在原来的基础上增加判断是否进行验证的开关
     *
     * @param model       要保存的实体
     * @param toValidator 是否对实体进行验证
     * @param <T>
     * @return
     */
    <T extends Model> Boolean update(T model, boolean toValidator);

}
