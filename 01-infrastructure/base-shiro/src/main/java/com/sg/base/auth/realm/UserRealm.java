package com.sg.base.auth.realm;

import com.sg.base.auth.service.BaseUserService;
import com.sg.base.bean.BeanFactory;
import com.sg.base.context.session.Session;
import com.sg.base.model.support.user.BaseUserModelSupport;
import org.apache.commons.lang.NotImplementedException;
import org.apache.shiro.authc.*;
import org.apache.shiro.authc.credential.PasswordService;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;

import java.util.Set;

/**
 * UserRealm
 *
 * @author Dai Wenqing
 * @date 2016/2/28
 */
public class UserRealm extends AuthorizingRealm {

    private BaseUserService baseUserService;// = new UserServiceImpl();
    private PasswordService passwordService;

    public void setPasswordService(PasswordService passwordService) {
        this.passwordService = passwordService;
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) throws AuthorizationException {
        String username = (String) principals.getPrimaryPrincipal();
        baseUserService = BeanFactory.getBean(BaseUserService.class);
        if (baseUserService == null)
            throw new NotImplementedException("BaseUserServiceSupport must be Implemented");
        Set<String> roles = baseUserService.findRoles(username);
        if (roles == null) {
            return null;
        }
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        authorizationInfo.setRoles(roles);
        return authorizationInfo;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        String key = (String) token.getPrincipal();

        baseUserService = BeanFactory.getBean(BaseUserService.class);
        Session session = BeanFactory.getBean(Session.class);
        if (baseUserService == null)
            throw new NotImplementedException("BaseUserServiceSupport must be Implemented");
        BaseUserModelSupport user = baseUserService.findByUsername(key);
        session.put("##user", user);
        if (user == null) {
            user = baseUserService.findByPhone(key);
            if (user == null) {
                user = baseUserService.findByIdCard(key);
                if (user == null) {
                    throw new UnknownAccountException();// 没找到帐号
                }
            }
        }
        if (Boolean.TRUE.equals(user.getLocked())) {
            throw new LockedAccountException(); // 帐号锁定
        }

        UsernamePasswordToken usernamePasswordToken = (UsernamePasswordToken) token;
        String username = String.valueOf(usernamePasswordToken.getUsername());
        // User user = userService.findByUserName(username);
        //passwordService.encryptPassword("123")
        SimpleAuthenticationInfo authenticationInfo = null;
        if (null != user) {
            String password = new String(usernamePasswordToken.getPassword());
            // 密码校验移交给了shiro的提供的一个接口实现类，所以这里注释掉
            authenticationInfo = new SimpleAuthenticationInfo(user.getUserName(), user.getPassword(), getName());
            authenticationInfo.setCredentialsSalt(ByteSource.Util.bytes(username + "_snow"));
        }
        return authenticationInfo;

        /*
         * // 交给AuthenticatingRealm使用CredentialsMatcher进行密码匹配
         * SimpleAuthenticationInfo authenticationInfo = new
         * SimpleAuthenticationInfo(user.getUserName(), // 用户名
         * user.getPassword(), // 密码 ByteSource.Util.bytes(user.getUserName() +
         * "_snow"), // salt=username+salt "userRealm"// realm name );
         * 
         * return authenticationInfo;
         */
    }

}
