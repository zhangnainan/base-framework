package com.sg.base.model.support.user.role;

import com.sg.base.model.support.BaseModel;

/**
 * BaseRoleModelSupport
 *
 * @author Liu Hongzhen
 * @date 2016/3/11.
 */
public interface BaseRoleModelSupport extends BaseModel {

    /**
     * 获取角色名
     *
     * @return
     */
    String getRoleName();

    void setRoleName(String roleName);

    /**
     * 获取目录ID
     *
     * @return
     */
    String getCatalogId();

    void setCatalogId(String catalogId);
}
