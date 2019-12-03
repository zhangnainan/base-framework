package com.sg.base.context.session;

import org.springframework.stereotype.Component;

/**
 * SessionImpl
 *
 * @author Dai Wenqing
 * @date 2016/1/27
 */
@Component("base.context.session")
public class SessionImpl implements Session, SessionAdapterAware {

    private ThreadLocal<SessionAdapter> sessionThreadLocal = new ThreadLocal<>();

    @Override
    public String getSessionId() {
        return sessionThreadLocal.get().getSessionId();
    }

    @Override
    public String getName() {
        if (sessionThreadLocal.get() != null)
            return sessionThreadLocal.get().getName();
        return "";
    }

    @Override
    public <T> T get(String key) {
        if (sessionThreadLocal.get() != null)
            return sessionThreadLocal.get().get(key);
        return null;
    }

    @Override
    public <T> void put(String key, Object o) {
        if (sessionThreadLocal.get() != null)
            sessionThreadLocal.get().put(key, o);
    }

    @Override
    public <T> void remove(String key) {
        if (sessionThreadLocal.get() != null)
            sessionThreadLocal.get().remove(key);
    }

    @Override
    public void setSession(SessionAdapter session) {
        /*if (Logger.isDebugEnable())
            Logger.info("session初始化（" + session.getName() + "）....");*/
        sessionThreadLocal.set(session);
    }
}
