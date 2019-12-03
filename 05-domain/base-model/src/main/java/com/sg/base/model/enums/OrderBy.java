package com.sg.base.model.enums;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * OrderByType
 *
 * @author Dai Wenqing
 * @date 2015/9/10
 */
public enum OrderBy {
    Asc("asc"), Desc("desc");
    private String type;

    OrderBy(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    private static Map<String, OrderBy> map;

    public static OrderBy get(String type) {
        if (map == null) {
            map = new ConcurrentHashMap<>();
            for (OrderBy dataType : OrderBy.values())
                map.put(dataType.getType(), dataType);
        }

        return map.get(type);
    }
}
