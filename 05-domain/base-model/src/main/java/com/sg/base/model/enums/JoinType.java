package com.sg.base.model.enums;

/**
 * FetchWay
 *
 * @author Dai Wenqing
 * @date 2016/5/30
 */
public enum JoinType {
    Left(" left join "), Right(" right join "), Inner(" inner join ");
    private String type;

    JoinType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
