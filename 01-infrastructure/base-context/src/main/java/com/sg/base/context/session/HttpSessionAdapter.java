package com.sg.base.context.session;

import com.sg.base.log.Logger;
import org.apache.commons.lang.NullArgumentException;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;

/**
 * 对原生会话进行适配
 *
 * @author Dai Wenqing
 * @date 2016/1/19
 */
@Component("base.context.session.http")
/* @Scope(BeanDefinition.SCOPE_PROTOTYPE) */
public class HttpSessionAdapter implements SessionAdapter, HttpSessionAware {
    private HttpSession httpSession;

    @Override
    public String getSessionId() {
        if (httpSession == null) {
            Logger.error(new NullArgumentException("httpSession"), error);
            return "";
        }
        return httpSession.getId();

    }

    @Override
    public String getName() {
        return "http";
    }

    @Override
    public <T> T get(String key) {
        if (httpSession == null) {
            Logger.error(new NullArgumentException("httpSession"), error);
            return null;
        }
        return (T) httpSession.getAttribute(key);
    }

    @Override
    public <T> void put(String key, Object o) {
        if (httpSession == null) {
            Logger.error(new NullArgumentException("httpSession"), error);
            return;
        }
        httpSession.setAttribute(key, o);
    }

    @Override
    public <T> void remove(String key) {
        if (httpSession == null) {
            Logger.error(new NullArgumentException("httpSession"), error);
            return;
        }
        httpSession.removeAttribute(key);
    }

    @Override
    public void set(HttpSession httpSession) {
        this.httpSession = httpSession;
    }
}
