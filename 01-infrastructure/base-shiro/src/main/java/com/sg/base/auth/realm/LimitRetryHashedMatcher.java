package com.sg.base.auth.realm;

import com.sg.base.auth.PasswordHelper;
import com.sg.base.cache.Cache;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.ExcessiveAttemptsException;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 限制登录次数，如果5次出错，锁定1个小时
 *
 * @author Dai Wenqing
 * @date 2016/5/16
 */
public class LimitRetryHashedMatcher extends HashedCredentialsMatcher {
    @Override
    public boolean doCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) {
        String username = (String) token.getPrincipal();
        // retrycount + 1

        Object element = Cache.getInstance().get(username);
        if (element == null) {
            Cache.getInstance().put(username, 1);
            element = 0;
        } else {
            int count = Integer.parseInt(element.toString()) + 1;
            element = count;
            Cache.getInstance().put(username, element);
        }
        AtomicInteger retryCount = new AtomicInteger(Integer.parseInt(element.toString()));
        if (retryCount.incrementAndGet() > 5) {
            // if retrycount >5 throw
            throw new ExcessiveAttemptsException();
        }
        boolean matches = false;
        // 匹配验证
        String sourcePassword = PasswordHelper.encryptPassword(token.getPrincipal().toString(), String.valueOf((char[]) (token.getCredentials())));
        matches = sourcePassword.equals(info.getCredentials());
        if (matches) {
            Cache.getInstance().remove(username);
        }
        return matches;
    }
}
