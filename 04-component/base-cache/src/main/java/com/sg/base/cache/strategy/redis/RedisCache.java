package com.sg.base.cache.strategy.redis;

import com.sg.base.bean.ContextRefreshedListener;
import com.sg.base.cache.CacheItemPriority;
import com.sg.base.cache.ExpirationWay;
import com.sg.base.cache.strategy.CacheStrategy;

/**
 * RedisCache
 *
 * @author Dai Wenqing
 * @date 2016/2/25
 */
public class RedisCache implements CacheStrategy, ContextRefreshedListener {
    @Override
    public String getName() {
        return null;
    }

    @Override
    public <T> void put(String key, T o) {

    }

    @Override
    public <T> void put(String key, T o, ExpirationWay expirationWay, long time) {

    }

    @Override
    public <T> void put(String key, T o, ExpirationWay expirationWay, long time, CacheItemPriority cacheItemPriority) {

    }

    @Override
    public <T> T get(String key) {
        return null;
    }

    @Override
    public void remove(String key) {

    }

    @Override
    public int getContextRefreshedSort() {
        return 7;
    }

    @Override
    public void onContextRefreshed() {

    }
}
