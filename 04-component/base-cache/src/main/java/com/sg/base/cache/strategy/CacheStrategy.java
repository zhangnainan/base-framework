package com.sg.base.cache.strategy;


import com.sg.base.cache.CacheItemPriority;
import com.sg.base.cache.ExpirationWay;

/**
 * CacheStrategy
 *
 * @author Dai Wenqing
 * @date 2016/1/18
 */
public interface CacheStrategy {

    /**
     * 获取缓存名称
     *
     * @return
     */
    public String getName();

    /**
     * 根据key添加缓存
     *
     * @param key 关键字
     * @param o   缓存对象
     * @param <T>
     */
    public <T> void put(String key, T o);

    /**
     * 根据时间过期类型添加缓存
     *
     * @param key           关键字
     * @param o             缓存对象
     * @param expirationWay 过期类型
     * @param time          过期时间
     * @param <T>
     */
    public <T> void put(String key, T o, ExpirationWay expirationWay, long time);

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
    public <T> void put(String key, T o, ExpirationWay expirationWay, long time, CacheItemPriority cacheItemPriority);

    /**
     * 根据关键字获取缓存
     *
     * @param key 关键字
     * @param <T>
     * @return
     */
    public <T> T get(String key);

    /**
     * 根据关键字删除缓存
     *
     * @param key 关键字
     */
    public void remove(String key);
}
