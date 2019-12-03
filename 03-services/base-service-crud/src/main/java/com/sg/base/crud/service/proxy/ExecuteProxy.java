package com.sg.base.crud.service.proxy;

import com.sg.base.crud.enums.DeleteType;
import com.sg.base.crud.service.ExecuteService;
import com.sg.base.dao.orm.Query;
import com.sg.base.model.Model;
import com.sg.base.model.enums.Criterion;
import com.sg.base.model.enums.Operator;

/**
 * ExecuteProxy
 *
 * @author Dai Wenqing
 * @date 2016/5/4
 */
public class ExecuteProxy {
    public enum ActionType {
        Delete, Update
    }

    private Query query;
    private ExecuteService executeService;
    //private CrudService crudService;
    private DeleteType deleteType;
    private ActionType actionType;

    public ExecuteProxy(Query query, ExecuteService deleteService) {
        this.query = query;
        this.executeService = deleteService;
        //this.crudService = CrudService.class.cast(executeService);
    }

    public ExecuteProxy update(Class<? extends Model> classZ) {
        this.actionType = ActionType.Update;
        query.from(classZ);
        return this;
    }

    public ExecuteProxy set(String column, Object value) {
        query.set(column, value);
        return this;
    }

    public ExecuteProxy delete(Class<? extends Model> classZ) {
        this.deleteType = DeleteType.Delete;
        this.actionType = ActionType.Delete;
        query.from(classZ);
        return this;
    }

    public ExecuteProxy recycle(Class<? extends Model> classZ) {
        this.deleteType = DeleteType.Recycle;
        this.actionType = ActionType.Delete;
        query.from(classZ);
        return this;
    }

    public ExecuteProxy remove(Class<? extends Model> classZ) {
        this.deleteType = DeleteType.Remove;
        this.actionType = ActionType.Delete;
        query.from(classZ);
        return this;
    }

    /**
     * 根据id进行回收数据
     *
     * @param classZ
     * @param id
     * @return
     */
    public boolean delete(Class<? extends Model> classZ, String id) {
        this.deleteType = DeleteType.Delete;
        this.actionType = ActionType.Delete;
        query.from(classZ);
        query.where("id", id);
        return executeService.deleteOrRecycle(query, DeleteType.Delete);
    }

    public boolean recycle(Class<? extends Model> classZ, String id) {
        this.deleteType = DeleteType.Recycle;
        this.actionType = ActionType.Delete;
        query.from(classZ);
        query.where("id", id);
        return executeService.deleteOrRecycle(query, DeleteType.Recycle);
    }

    public boolean remove(Class<? extends Model> classZ, String id) {
        this.deleteType = DeleteType.Remove;
        this.actionType = ActionType.Delete;
        query.from(classZ);
        query.where("id", id);
        return executeService.deleteOrRecycle(query, DeleteType.Remove);
    }

    public ExecuteProxy where(String column, Criterion criterion, Object value, Operator... operator) {
        query.where(column, criterion, value, operator);
        return this;
    }

    public ExecuteProxy where(String column, Object value, Operator... operator) {
        where(column, Criterion.Equals, value, operator);
        return this;
    }

    public ExecuteProxy where(String where) {
        query.where(where);
        return this;
    }

    public ExecuteProxy where(String column, Criterion criterion, Object[] value, Operator... operator) {
        query.where(column, criterion, value, operator);
        return this;
    }

    /*
     * public <T extends Model> PageList<T> list(int page, int size, Object...
     * args) { Sql sqlOrm = BeanFactory.getBean(Sql.class); return
     * sqlOrm.getList(this.classZ, this.sql, size, page, args); }
     */

    public boolean invoke(Object... args) {
        if (actionType == ActionType.Delete)
            return executeService.deleteOrRecycle(query, deleteType);
        else if (actionType == ActionType.Update)
            return executeService.update(query, args);
        return false;
    }

}
