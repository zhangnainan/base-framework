package com.sg.base.crud.exception;

import java.util.ArrayList;
import java.util.List;

/**
 * ExistsException
 *
 * @author Dai Wenqing
 * @date 2016/5/25
 */
public class ExistsException extends RuntimeException {
    /**
     * 字段代码
     */
    private Object[] code;

    public String getCode() {
        List<String> codeList = new ArrayList<>();
        for (Object o : code) {
            codeList.add(o.toString());
        }
        return String.join(",", codeList);
    }

    public ExistsException(Object... code) {
        super();
        this.code = code;
    }
}
