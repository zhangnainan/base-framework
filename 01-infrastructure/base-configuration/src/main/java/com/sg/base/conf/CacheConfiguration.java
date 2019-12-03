package com.sg.base.conf;

/**
 * 缓存配置
 *
 * @author Dai Wenqing
 * @date 2016/1/18
 */
public interface CacheConfiguration {
    /**
     * 获取缓存所使用的对象
     *
     * @return
     */
    default String getCacheName() {
        return "local";
    }

}
