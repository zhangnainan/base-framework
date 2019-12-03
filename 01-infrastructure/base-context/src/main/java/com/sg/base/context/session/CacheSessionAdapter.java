package com.sg.base.context.session;

import com.sg.base.cache.Cache;
import com.sg.base.cache.ExpirationWay;
import com.sg.base.log.Logger;
import org.apache.commons.lang.NullArgumentException;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;

/**
 * CacheSessionAdapter
 *
 * @author Dai Wenqing
 * @date 2016/1/19
 */
@Component("base.context.session.cache")
/* @Scope(BeanDefinition.SCOPE_PROTOTYPE) */
public class CacheSessionAdapter implements SessionAdapter, HttpSessionAware {
    private HttpSession httpSession;

    @Override
    public void set(HttpSession httpSession) {
        this.httpSession = httpSession;
    }

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
        return "cache";
    }

    @Override
    public <T> T get(String key) {
        if (httpSession == null) {
            Logger.error(new NullArgumentException("httpSession"), error);
            return null;
        }
        return Cache.getInstance().get(httpSession.getId() + "_" + key);
    }

    @Override
    public <T> void put(String key, Object o) {
        if (httpSession == null) {
            Logger.error(new NullArgumentException("httpSession"), error);
            return;
        }
        Cache.getInstance().put(httpSession.getId() + "_" + key, o, ExpirationWay.SlidingTime, httpSession.getMaxInactiveInterval() * 1000);
    }

    @Override
    public <T> void remove(String key) {
        if (httpSession == null) {
            Logger.error(new NullArgumentException("httpSession"), error);
            return;
        }
        Cache.getInstance().remove(httpSession.getId() + "_" + key);
    }
}
