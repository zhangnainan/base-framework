package com.sg.base.dao.orm;

import com.sg.base.model.enums.Criterion;
import com.sg.base.model.enums.Operator;

/**
 * WhereContext
 *
 * @author Dai Wenqing
 * @date 2016/9/27
 */
public class WhereContext {
    private String key;
    private Object[] value;
    private Criterion criterion;
    private Operator operator;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Object[] getValue() {
        return value;
    }

    public void setValue(Object... value) {
        this.value = value;
    }

    public Criterion getCriterion() {
        return criterion;
    }

    public void setCriterion(Criterion criterion) {
        this.criterion = criterion;
    }

    public Operator getOperator() {
        return operator;
    }

    public void setOperator(Operator operator) {
        this.operator = operator;
    }
}
