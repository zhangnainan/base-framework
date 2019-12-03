package com.sg.base.auth.permission;

import org.apache.shiro.authz.Permission;

import java.io.Serializable;

/**
 * 字符串权限验证，字符串匹配成功，则验证成功，否则失败
 *
 * @author lhz
 * @date 2016/3/23.
 */
public class StringPermission implements Permission, Serializable {

    private String permissionInfo;

    public StringPermission(String permissionInfo) {
        this.permissionInfo = permissionInfo;
    }

    @Override
    public boolean implies(Permission permission) {
        if (!(permission instanceof StringPermission)) {
            return false;
        }
        StringPermission srcPermission = (StringPermission) permission;
        if (this.permissionInfo.equals(srcPermission.permissionInfo)) {
            return true;
        }
        return false;
    }
}
