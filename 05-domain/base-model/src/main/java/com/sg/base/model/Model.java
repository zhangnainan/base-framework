package com.sg.base.model;

import java.io.Serializable;

/**
 * 持久化模型定义。
 *
 * @author lpw
 */
public interface Model extends Serializable {
    /**
     * 获得Model ID值。
     *
     * @return Model ID值。
     */
    String getId();

    /**
     * 设置Model ID值。
     *
     * @param id Model ID值。
     */
    void setId(String id);
}
