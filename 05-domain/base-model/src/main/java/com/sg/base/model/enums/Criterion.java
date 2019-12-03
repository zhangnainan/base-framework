package com.sg.base.model.enums;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by lpw on 15-8-14.
 */
public enum Criterion {
    /**
     * 等于。
     */
    Equals(" =?"),
    /**
     * 大于。
     */
    GreaterThan(" >?"),
    /**
     * 大于等于。
     */
    GreaterThanOrEquals(" >=?"),
    /**
     * 小于。
     */
    LessThan(" <?"),
    /**
     * 小于等于。
     */
    LessThanOrEquals(" <=?"),
    /**
     * like "%string%"。
     */
    Like(" like ?"),
    /**
     * like "string%"。
     */
    StartsWith(" like ?"),
    /**
     * like "%string"。
     */
    EndsWith(" like ?"),
    /**
     * 为null。
     */
    IsNull(" is null"),
    /**
     * 非null。
     */
    IsNotNUll(" is not null"),

    /**
     * 介于。
     */
    Between(" between ? and ?"),

    In(" in"),

    NotIN(" not in"),

    NotEqual(" != ?");

    private String type;

    Criterion(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    private static Map<String, Criterion> map;

    public static Criterion get(String type) {
        if (map == null) {
            map = new ConcurrentHashMap<>();
            for (Criterion dataType : Criterion.values())
                map.put(dataType.getType(), dataType);
        }

        return map.get(type);
    }
}
