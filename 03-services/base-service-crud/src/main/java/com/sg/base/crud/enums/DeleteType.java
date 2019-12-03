package com.sg.base.crud.enums;

/**
 * Created by dwq676 on 2015/8/19.
 */
public enum DeleteType {

    /**
     * 被删除，但放入回收站数据，可从界面上恢复为正常数据
     */
    Delete(0),

    /**
     * 恢复数据到回收站
     */
    Recycle(1),

    /**
     * 从回收站被彻底删除数据，无法从界面上进行恢复，数据库管理员可恢复
     */
    Remove(-1);

    private int type;

    DeleteType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
