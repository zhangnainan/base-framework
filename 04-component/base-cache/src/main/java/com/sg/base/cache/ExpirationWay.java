package com.sg.base.cache;

/**
 * 过期类型或方式
 *
 * @author Dai Wenqing
 * @date 2016/1/18
 */
public enum ExpirationWay {
    /**
     * 绝对时间
     */
    AbsoluteTime,
    /**
     * 相对当前里再滑过的时间
     */
    SlidingTime
}
