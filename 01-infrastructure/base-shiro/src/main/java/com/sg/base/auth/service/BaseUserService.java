package com.sg.base.auth.service;

import com.sg.base.model.support.user.BaseUserModelSupport;

import java.util.Set;

/**
 * BaseUserServiceSupport
 *
 * @author Dai Wenqing
 * @date 2016/2/28
 */
public interface BaseUserService {
    /*BaseUserModelSupport createUser(BaseUserModelSupport user); // 创建账户

    void changePassword(Long userId, String newPassword);// 修改密码

    void correlationRoles(Long userId, Long... roleIds); // 添加用户-角色关系

    void unCorrelationRoles(Long userId, Long... roleIds);// 移除用户-角色关系*/

    BaseUserModelSupport findByUsername(String username);// 根据用户名查找用户

    BaseUserModelSupport findByPhone(String phone);

    BaseUserModelSupport findByUserId(String id);

    BaseUserModelSupport findByIdCard(String idCard);

    Set<String> findRoles(String username);// 根据用户名查找其角色
}
