package com.sg.base.log;

/**
 * LogType
 *
 * @author Dai Wenqing
 * @date 2016/10/9
 */
public enum LogType {
    System("system");

    protected String type;

    LogType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
