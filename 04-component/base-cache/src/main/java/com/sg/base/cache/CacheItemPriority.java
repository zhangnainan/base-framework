package com.sg.base.cache;

/**
 * CacheItemPriority
 *
 * @author Dai Wenqing
 * @date 2016/1/18
 */
public enum CacheItemPriority {

    Low(0),
    //
    // 摘要:
    //     CacheStrategy items with this priority level are more likely to be deleted from the cache
    //     as the server frees system memory than items assigned a System.Web.Caching.CacheItemPriority.Normal
    //     priority.
    BelowNormal(1),
    //
    // 摘要:
    //     CacheStrategy items with this priority level are likely to be deleted from the cache
    //     as the server frees system memory only after those items with System.Web.Caching.CacheItemPriority.Low
    //     or System.Web.Caching.CacheItemPriority.BelowNormal priority. This is the default.
    Default(2),
    //
    // 摘要:
    //     CacheStrategy items with this priority level are the least likely to be deleted from
    //     the cache as the server frees system memory.
    High(3),
    //
    // 摘要:
    //     The cache items with this priority level will not be automatically deleted from
    //     the cache as the server frees system memory. However, items with this priority
    //     level are removed along with other items according to the item's absolute or
    //     sliding expiration time.
    NotRemovable(4);


    public int getValue() {
        return value;
    }

    private int value = 0;

    private CacheItemPriority(int value) {
        this.value = value;
    }


}
