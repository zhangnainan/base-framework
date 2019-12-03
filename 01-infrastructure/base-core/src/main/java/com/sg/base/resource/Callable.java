package com.sg.base.resource;

/**
 * 可执行，可回调用接口
 * 带有返回值的匿名执行函数
 *
 * @author Dai Wenqing
 * @date 2016/6/17
 */
@FunctionalInterface
public interface Callable<T> {
    T call();
}
