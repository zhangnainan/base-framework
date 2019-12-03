package com.sg.base.conf.spare;

import com.sg.base.conf.CacheConfiguration;

/**
 * 配置备用类，用于接口中默认的配置信息
 * 防止在在引用配置模块
 * 未引用配置类造成的异常，保证代码正常运行；
 * 同时提醒用户引用配置配置模块
 *
 * @author Dai Wenqing
 * @date 2016/1/26
 */
public class CacheConfigurationImpl implements CacheConfiguration {

}
