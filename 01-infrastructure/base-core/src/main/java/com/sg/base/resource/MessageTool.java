/**
 *
 */
package com.sg.base.resource;


/**
 * 资源。用于获取项目中message.properties中的资源。
 *
 * @author lpw
 */
public interface MessageTool {
    /**
     * 获得资源。
     *
     * @param key  资源key。
     * @param args 资源参数集。
     * @return 资源。
     */
    String get(String key, Object... args);
}
