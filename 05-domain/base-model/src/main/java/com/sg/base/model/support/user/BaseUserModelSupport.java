package com.sg.base.model.support.user;

import com.sg.base.model.support.BaseModel;

/**
 * BaseUserModelSupport
 *
 * @author Dai Wenqing
 * @date 2015/9/14
 */
public interface BaseUserModelSupport extends BaseModel {

    String getUserName();

    void setUserName(String username);

    /*
     * String getDomain();
     * 
     * String setDomain(String domain);
     */

    String getPassword();

    void setPassword(String password);

    /**
     * 是否被锁住
     *
     * @return
     */
    boolean getLocked();

    void setLocked(boolean locked);

    /**
     * 一个账户是否允许多个地方同时登录
     *
     * @return
     */
    boolean getMulti();

    void setMulti(boolean multi);

    String getSalt();

    void setSalt(String salt);
}
