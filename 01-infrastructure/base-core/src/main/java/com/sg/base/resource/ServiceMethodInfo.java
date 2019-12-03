package com.sg.base.resource;

import java.lang.reflect.Method;
import java.util.List;

/**
 * ServiceMethodInfo
 *
 * @author Dai Wenqing
 * @date 2016/6/16
 */
public interface ServiceMethodInfo {
    /**
     * 根据方法名称获取方法，有重载方法的可能
     *
     * @param methodName 方法名称
     * @return
     */
    List<Method> getMethod(String methodName);

    /**
     * 根据方法名称及参数个数获取方法
     *
     * @param methodName 方法名
     * @param argCount   参数个数
     * @return
     */
    Method getMethod(String methodName, int argCount);
}
