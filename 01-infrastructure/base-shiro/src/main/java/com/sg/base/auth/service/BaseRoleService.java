package com.sg.base.auth.service;

import com.sg.base.model.support.user.role.BasePermissionSupport;

import java.util.Set;

/**
 * @author lhz
 * @date 2016/3/23
 */
public interface BaseRoleService {

    /**
     * 获取角色分发的权限信息
     *
     * @param roleId 角色Id
     * @return
     */
    Set<BasePermissionSupport> findPermissions(String roleId); // 根据用户名查找其权限
}
