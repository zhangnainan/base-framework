package com.sg.base.context.request;

import com.sg.base.model.Model;

import java.util.Date;
import java.util.Map;

/**
 * Request
 *
 * @author Dai Wenqing
 * @date 2016/1/19
 */
public interface Request {
    /**
     * 获得请求参数值。
     *
     * @param name 参数名称。
     * @return 参数值。
     */
    String get(String name);


    /**
     * 获得整型请求参数值。
     *
     * @param name 参数名称。
     * @return 整型参数值。
     */
    int getAsInt(String name);

    /**
     * 获得整型请求参数值。
     *
     * @param name 参数名称。
     * @return 整型参数值。
     */
    long getAsLong(String name);

    /**
     * 获得日期型请求参数值。
     *
     * @param name 参数名称。
     * @return 日期型参数值。
     */
    Date getAsDate(String name);

    /**
     * 获得所有请求参数值对。
     *
     * @return 请求参数值对。
     */
    Map<String, String> getMap();

    /**
     * 获取InputStream中的数据。
     *
     * @return InputStream中的数据；如果不存在则返回空字符串。
     */
    String getFromInputStream();

    /**
     * 将请求参数集保存到Model实例中。
     *
     * @param model Model实例。
     */
    <T extends Model> void setToModel(T model);

    /**
     * 验证消息摘要是否合法。
     *
     * @return 如果验证通过则返回true；否则返回false。
     */
    boolean checkSign();

    /**
     * 获取服务器名。
     *
     * @return 服务器名。
     */
    String getServerName();

    /**
     * 获取服务器端口号。
     *
     * @return 服务器端口号。
     */
    int getServerPort();

    /**
     * 获取部署项目名。
     *
     * @return 部署项目名。
     */
    String getContextPath();

    String getRealPath(String path);

    /**
     * 获取请求URI。
     *
     * @return 请求URI。
     */
    String getUri();

    /**
     * 获取请求方法。
     *
     * @return 请求方法。
     */
    String getMethod();

    /**
     * 根据关键字获取头部信息
     *
     * @param key
     * @return
     */
    String getHeader(String key);

    /**
     * 获取IP值
     *
     * @return
     */
    String getIp();

    Map<String, String> getHeaderMap();
}
