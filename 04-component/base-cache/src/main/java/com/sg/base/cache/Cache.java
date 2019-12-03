package com.sg.base.cache;

import com.sg.base.bean.BeanFactory;
import com.sg.base.bean.ContextRefreshedListener;
import com.sg.base.cache.strategy.CacheStrategy;
import com.sg.base.conf.CacheConfiguration;
import com.sg.base.conf.spare.CacheConfigurationImpl;
import com.sg.base.log.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * CacheStrategy
 *
 * @author Dai Wenqing
 * @date 2016/1/18
 */
@Component("base.cache")
public class Cache implements ContextRefreshedListener {
    private static Cache instance;
    private static Object lockObj = new Object();
    /**
     * 缓存策略
     */
    private CacheStrategy cacheStrategy;
    @Autowired
    private Set<CacheStrategy> cacheStrategySet;
    @Autowired(required = false)
    private CacheConfiguration cacheConfiguration;

    public static Cache getInstance() {
        synchronized (lockObj) {
            if (instance == null) {
                instance = BeanFactory.getBean(Cache.class);
            }
            return instance;
        }
    }

    /**
     * 根据key添加永久缓存
     *
     * @param key 关键字
     * @param o   缓存对象
     * @param <T>
     */
    public <T> void put(String key, T o) {
        cacheStrategy.put(key, o);
    }

    /**
     * 根据时间过期类型添加缓存
     *
     * @param key           关键字
     * @param o             缓存对象
     * @param expirationWay 过期类型
     * @param time          过期时间
     * @param <T>
     */
    public <T> void put(String key, T o, ExpirationWay expirationWay, long time) {
        cacheStrategy.put(key, o, expirationWay, time);
    }

    /**
     * 根据时间过期类型以及缓存优先级添加缓存
     *
     * @param key               关键字
     * @param o                 缓存对象
     * @param expirationWay     过期类型
     * @param time              过期时间
     * @param cacheItemPriority 缓行级别
     * @param <T>
     */
    public <T> void put(String key, T o, ExpirationWay expirationWay, long time, CacheItemPriority cacheItemPriority) {
        cacheStrategy.put(key, o, expirationWay, time, cacheItemPriority);
    }

    /**
     * 根据关键字获取缓存
     *
     * @param key 关键字
     * @param <T>
     * @return
     */
    public <T> T get(String key) {
        return cacheStrategy.get(key);
    }

    /**
     * 根据关键字删除缓存
     *
     * @param key 关键字
     */
    public void remove(String key) {
        cacheStrategy.remove(key);
    }

    @Override
    public int getContextRefreshedSort() {
        return 5;
    }

    @Override
    public void onContextRefreshed() {
        if (cacheConfiguration == null) {
            cacheConfiguration = new CacheConfigurationImpl();

        }
        if (Logger.isDebugEnable())
            Logger.debug("使用[{}]缓存处理器。", cacheConfiguration.getCacheName());

        for (CacheStrategy strategy : cacheStrategySet) {
            if (strategy.getName().equals(cacheConfiguration.getCacheName())) {
                this.cacheStrategy = strategy;
                break;
            }
        }

        if (cacheStrategy == null)
            Logger.warn(null, "无法获得缓存处理器[{}]。", cacheConfiguration.getCacheName());
    }

}
