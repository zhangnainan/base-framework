package com.sg.base.dao.hibernate;

/**
 * ExistsException
 *
 * @author Dai Wenqing
 * @date 2016/5/25
 */
public class QueryCastException extends RuntimeException {
    private String message;

    public String getCode() {
        return message;
    }

    public QueryCastException(String message) {
        super();
        this.message = message;
    }
}
