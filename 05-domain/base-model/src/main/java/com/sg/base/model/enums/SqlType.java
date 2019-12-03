package com.sg.base.model.enums;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * SqlType
 *
 * @author Dai Wenqing
 * @date 2015/8/31
 */
public enum SqlType {

    Mysql("mysql"),

    Oracle("oracle"),

    Sqlsever("sqlserver");

    private String type;

    SqlType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    private static Map<String, SqlType> map;

    public static SqlType get(String type) {
        if (map == null) {
            map = new ConcurrentHashMap<>();
            for (SqlType dataType : SqlType.values())
                map.put(dataType.getType(), dataType);
        }

        return map.get(type);
    }
}
