package com.sg.base.cache.strategy;

import com.sg.base.cache.CacheItemPriority;
import com.sg.base.cache.Element;
import com.sg.base.cache.ExpirationWay;
import com.sg.base.log.Logger;
import com.sg.base.util.Validator;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 本地缓存 开启一个独立的线程定期清理缓存
 *
 * @author Dai Wenqing
 * @date 2016/1/18
 */
@Component("base.cache.strategy.local")
public class LocalCache implements CacheStrategy {

    private int jobTime = 60000;
    protected Map<String, Element> map = new ConcurrentHashMap<>();

    /**
     * 用于计数系统运行分钟数
     * <p>
     * private long ndx = 0; private boolean starting = true;
     */
    @Scheduled(cron = "0/5 * *  * * ? ")
    public void run() {

        // 定时清理过期的缓存或根据策略删除缓存
        clearExpiration();
        lru();
        // 系统回收内存
        System.gc();

    }

    /**
     * 移动过期缓存
     */
    private void clearExpiration() {
        Set<String> obsoletes = new HashSet<>();
        int ndx = 0;
        // List<String> keyList = new ArrayList<>();
        for (String key : map.keySet()) {
            // 时间间隔等于-1 表示永不过期
            // Logger.info("###################开始清理缓存###################");
            if (map.get(key).getSpanTime() > 0) {
                if (map.get(key).getExpiration() < System.currentTimeMillis()) {
                    obsoletes.add(key);
                    ndx++;
                }
            }
        }
        if (ndx > 0)
            Logger.info("共清了缓存：" + ndx + "个" + ",[" + String.join(",", obsoletes) + "]");
        obsoletes.forEach(map::remove);
    }

    /**
     * 最近最少使用使用内存清理
     */
    private void lru() {
        /**
         * 当缓存达到分配内存大小时，进行缓存清理 按清理优先进行排序 从低优先级开始且使用次数最少的进行移除
         */
        //// TODO: 2016/1/18 内存清理最近最少使用算法，待实现
    }

    @Override
    public String getName() {
        return "local";
    }

    @Override
    public <T> void put(String key, T o) {
        put(key, o, ExpirationWay.SlidingTime, -1, CacheItemPriority.Default);
    }

    @Override
    public <T> void put(String key, T o, ExpirationWay expirationWay, long time) {
        put(key, o, expirationWay, time, CacheItemPriority.Default);
    }

    @Override
    public <T> void put(String key, T o, ExpirationWay expirationWay, long time, CacheItemPriority cacheItemPriority) {

        if (o == null)
            return;
        if (Validator.isEmpty(key))
            return;

        Element element = new Element();
        element.setCreateTime(System.currentTimeMillis());
        element.setVisitTimes(0);
        element.setUpdateTime(System.currentTimeMillis());
        element.setKey(key);
        element.setValue(o);
        element.setPriority(cacheItemPriority.getValue());
        if (time > 0)
            element.setExpiration(System.currentTimeMillis() + time);
        element.setSpanTime(time);
        element.setExpirationWay(expirationWay);

        map.put(key, element);
    }

    @Override
    public <T> T get(String key) {
        if (Validator.isEmpty(key))
            return null;
        Element element = map.get(key);
        if (element == null)
            return null;
        /**
         * 过期方式为相对方式 每获取一次当前的过期时间应该往后推一个时间周期
         */
        if (element.getExpirationWay() == ExpirationWay.SlidingTime) {
            element.setExpiration(System.currentTimeMillis() + element.getSpanTime());
            element.setUpdateTime(System.currentTimeMillis());
        }
        /**
         * 增加访问次数,修改最后访问时间
         */
        element.setVisitTimes(element.getVisitTimes() + 1);
        element.setLastVisitedTime(System.currentTimeMillis());

        map.put(key, element);

        if (element != null)
            return (T) element.getValue();
        return null;
    }

    @Override
    public void remove(String key) {
        if (Validator.isEmpty(key))
            return;
        Element element = map.get(key);
        if (element != null)
            map.remove(key);
    }
}
