package com.sg.base.cache;

import java.io.Serializable;

/**
 * 缓存元素。
 *
 * @author lpw
 */
public class Element<T> implements Comparable<Element>, Serializable {
    private static final long serialVersionUID = 2825385973125777861L;

    protected String key;
    protected T value;
    protected boolean resident;
    protected long lastVisitedTime;
    /**
     * 创建时间
     */
    protected long createTime;
    /**
     * 更新时间
     */
    protected long updateTime;
    /**
     * 访问次数
     */
    protected long visitTimes = 0;

    /**
     * 过期方式
     */
    protected ExpirationWay expirationWay;

    public ExpirationWay getExpirationWay() {
        return expirationWay;
    }

    public void setExpirationWay(ExpirationWay expirationWay) {
        this.expirationWay = expirationWay;
    }

    /**
     * 下一次过期具体时间
     */
    protected long expiration = 0;

    /**
     * 过期间隔时间
     * -1=表示永过期
     */
    protected long spanTime = -1;

    public long getSpanTime() {
        return spanTime;
    }

    public void setSpanTime(long spanTime) {
        this.spanTime = spanTime;
    }

    public long getExpiration() {
        return expiration;
    }

    public void setExpiration(long expiration) {
        this.expiration = expiration;
    }

    /**
     * 优先级
     */
    protected int priority = 0;


    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }


    public long getVisitTimes() {
        return visitTimes;
    }

    public void setVisitTimes(long visitTimes) {
        this.visitTimes = visitTimes;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public boolean isResident() {
        return resident;
    }

    public void setResident(boolean resident) {
        this.resident = resident;
    }

    public long getLastVisitedTime() {
        return lastVisitedTime;
    }

    public void setLastVisitedTime(long lastVisitedTime) {
        this.lastVisitedTime = lastVisitedTime;
    }

    @Override
    public int compareTo(Element o) {
        if (lastVisitedTime == o.lastVisitedTime)
            return 0;

        return lastVisitedTime > o.lastVisitedTime ? 1 : -1;
    }
}
