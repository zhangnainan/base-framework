package com.sg.base.bean;

/**
 * 注册需要扫描bean对象
 *
 * @author Dai Wenqing
 * @date 2016/6/16
 */
public interface BeansScanRegister {

    void before();

    void run(String beanName);

    void after();
}
