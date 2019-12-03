/**
 *
 */
package com.sg.base.model;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lpw
 */
@Component("snow.model.page-list")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class PageList<T extends Model> extends Page<T> {

    @Value("${commons.dao.sql.page-size.max:100}")
    protected int maxPageSize;
    protected List<T> list = new ArrayList<>();
    //protected int count;
    //protected int size;
    //protected int number;

    /*
     * public void setPage(int count, int size, int number) { this.count =
     * Math.max(0, count); this.size = Math.min(maxPageSize, size < 1 ? 20 :
     * size); this.number = Math.min(number, this.count / this.size +
     * (this.count % this.size == 0 ? 0 : 1)); this.number = Math.max(1,
     * this.number); }
     */

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    @Override
    public T getData() {
        return null;
    }

    public JSONObject toJson() {
        JSONObject object = new JSONObject();
        object.put("total", count);
        object.put("size", size);
        object.put("number", number);
        JSONArray array = new JSONArray();
        for (T model : list)
            array.add(ModelHelper.toJson(model));
        object.put("rows", array);

        return object;
    }
}
