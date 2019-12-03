package com.sg.base.resource;

import com.sg.base.bean.BeanFactory;
import com.sg.base.util.Validator;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 服务方法信息 对服务方法进行分解集中管理
 *
 * @author Dai Wenqing
 * @date 2016/6/16
 */
@Component("base.resource.service.info")
public class ServiceMethodInfoImpl implements ServiceMethodInfo {
    // 根据方法名称及参数个数进行缓存
    private Map<Map<String, Integer>, Method> methodMap = new ConcurrentHashMap<>();
    private Map<String, List<Method>> methodListMap = new ConcurrentHashMap<>();
    @Autowired(required = false)
    private Set<ServiceRegisterAble> serviceRegisterAbleSet;

    private void register(String beanName) {
        if (!Validator.isEmpty(serviceRegisterAbleSet)) {
            serviceRegisterAbleSet.forEach(c -> {

            });
        }
        if (beanName.endsWith("service")) {
            Object o = BeanFactory.getBean(beanName);
            if (!Validator.isEmpty(o)) {
                Method[] methods = o.getClass().getDeclaredMethods();
                for (Method m : methods) {
                    Map<String, Integer> map = new HashedMap();
                    map.put(m.getName(), m.getParameterCount());
                    methodMap.put(map, m);

                    List<Method> methodList = methodListMap.get(m.getName());
                    if (Validator.isEmpty(methodList)) {
                        methodList = new ArrayList<>();
                        methodListMap.put(m.getName(), methodList);
                    }
                    methodList.add(m);
                }
            }
        }
    }

    @Override
    public List<Method> getMethod(String methodName) {
        if (Validator.isEmpty(methodName))
            return new ArrayList<>();
        return methodListMap.get(methodName);
    }

    @Override
    public Method getMethod(String methodName, int argCount) {
        if (Validator.isEmpty(methodName))
            return null;
        Map<String, Integer> nameCountMap = new HashedMap();
        nameCountMap.put(methodName, argCount);
        return methodMap.get(nameCountMap);
    }

}
