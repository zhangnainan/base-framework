package com.sg.base.auth.permission;

import com.sg.base.auth.service.BaseRoleService;
import com.sg.base.bean.BeanFactory;
import com.sg.base.model.support.user.role.BasePermissionSupport;
import org.apache.commons.lang.NotImplementedException;
import org.apache.shiro.authz.Permission;
import org.apache.shiro.authz.permission.RolePermissionResolver;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Created by Administrator on 2016/3/14.
 */
public class BaseRolePermissionResolver implements RolePermissionResolver {
    private BaseRoleService baseRoleService;

    @Override
    public Collection<Permission> resolvePermissionsInRole(String roleString) {
        baseRoleService = BeanFactory.getBean(BaseRoleService.class);
        if (baseRoleService == null)
            throw new NotImplementedException("BaseRoleServiceSupport must be Implemented");
        Set<BasePermissionSupport> permissions = baseRoleService.findPermissions(roleString);
        if (permissions == null) {
            return null;
        }
        List list = new ArrayList();
        for (BasePermissionSupport permission : permissions) {
            if (permission.getType().equals("StringPermission")) {
                list.add(new StringPermission(permission.getPermission()));
            }
        }
        return list;
    }
}
