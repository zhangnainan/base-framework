package com.sg.base.context;

import com.sg.base.context.session.Session;
import com.sg.base.model.support.user.BaseUserModelSupport;
import com.sg.base.model.support.user.UserHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * UserHelperImpl
 *
 * @author Dai Wenqing
 * @date 2016/3/7
 */
@Component("snow.service.user.helper")
public class UserHelperImpl implements UserHelper {
    @Autowired
    private Session session;

    @Override
    public String getUserId() {
        if (session != null) {
            BaseUserModelSupport baseUserModelSupport = session.get("##user");
            if (baseUserModelSupport != null)
                return baseUserModelSupport.getId();
        }
        return "";
    }

    @Override
    public String getUsername() {
        if (session != null) {
            BaseUserModelSupport baseUserModelSupport = session.get("##user");
            if (baseUserModelSupport != null)
                return baseUserModelSupport.getUserName();
        }
        return "";
    }

    @Override
    public String getDomain() {
        if (session != null) {
            BaseUserModelSupport baseUserModelSupport = session.get("##user");
            if (baseUserModelSupport != null)
                return baseUserModelSupport.getDomain();
        }
        return "0";
    }
}
