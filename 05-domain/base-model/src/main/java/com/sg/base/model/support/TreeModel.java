package com.sg.base.model.support;

import com.sg.base.model.Model;
import com.sg.base.model.annotation.Jsonable;

/**
 * Created by wangyangcheng on 2016/5/16.
 */
public class TreeModel implements Model {

    private String id;
    private String code;
    private String pid;
    private String parentCode;
    private String text;

    public TreeModel(String id, String code, String pid, String text) {

        this.id = id;
        this.code = code;
        this.pid = pid;
        this.text = text;
    }

    public TreeModel() {
    }

    @Jsonable
    public String getParentCode() {
        return parentCode;
    }

    public void setParentCode(String parentCode) {
        this.parentCode = parentCode;
    }

    @Jsonable
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Jsonable
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Jsonable
    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    @Jsonable
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
