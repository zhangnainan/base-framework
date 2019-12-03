package com.sg.base.conf.property;

import java.util.Map;

/**
 * ConfigurationImpl
 *
 * @ Dai Wenqing
 * @date 2016/1/6
 */
public interface Configuration {
    /**
     * 获取所有配置元素
     *
     * @return 返回一个或多个对象配置，key:对象名，value：对象配置
     */
    Map<String, Element> getConfigureElements();

    Object setConfigure(Map<String, Element> configMap);
}
