package com.sg.base.model;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Page
 *
 * @author Dai Wenqing
 * @date 2015/10/4
 */
@Component("base.model.page")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class Page<T> {

    protected int count;
    protected int size;
    protected int number;
    protected T model;

    /**
     * 设置分页信息。
     *
     * @param count  记录总数。
     * @param size   每页显示记录数。
     * @param number 当前显示页码数。
     */
    public void setPage(int count, int size, int number) {
        this.count = Math.max(0, count);
        this.size = Math.max(1, size);
        this.number = Math.min(number, this.count / this.size + (this.count % this.size == 0 ? 0 : 1));
        this.number = Math.max(1, this.number);
    }

    /**
     * 获取记录总数。
     *
     * @return 记录总数。
     */
    public int getCount() {
        return count;
    }

    /**
     * 获取每页最大显示记录数。
     *
     * @return 每页最大显示记录数。
     */
    public int getSize() {
        return size;
    }

    /**
     * 获取当前显示页数。
     *
     * @return 当前显示页数。
     */
    public int getNumber() {
        return number;
    }

    /**
     * 获取数据集。
     *
     * @return 数据集。
     */
    public T getData() {
        return model;
    }

    /**
     * 设置数据集。
     *
     * @param model 数据集。
     */
    public void setData(T model) {
        this.model = model;
    }

    /**
     * 转化为JSON格式的数据。
     *
     * @return JSON格式的数据。
     */
    public JSONObject toJson() {
        JSONObject object = new JSONObject();
        object.put("count", count);
        object.put("size", size);
        object.put("number", number);
        JSONArray array = new JSONArray();
        /*
         * for (T model : list) array.add(modelHelper.toJson(model));
         */
        array.add(JSONArray.fromObject(this.model));
        object.put("list", array);

        return object;
    }
}
