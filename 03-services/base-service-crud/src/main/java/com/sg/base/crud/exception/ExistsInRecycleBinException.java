package com.sg.base.crud.exception;

import java.util.ArrayList;
import java.util.List;

/**
 * ExistsInRecycleBinException
 *
 * @author Dai Wenqing
 * @date 2016/5/22
 */
public class ExistsInRecycleBinException extends RuntimeException {
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

    public ExistsInRecycleBinException(Object... code) {
        super();
        this.code = code;
    }
}
