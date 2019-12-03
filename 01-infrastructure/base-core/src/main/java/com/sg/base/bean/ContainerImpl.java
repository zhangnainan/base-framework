package com.sg.base.bean;

import com.sg.base.log.Logger;
import com.sg.base.util.Validator;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @author lpw
 */
@Component("base.bean.container")
public class ContainerImpl implements Container, ApplicationListener<ApplicationEvent>, ApplicationContextAware {

    @Autowired(required = false)
    protected List<ContextRefreshedListener> refreshedListeners;
    @Autowired(required = false)
    protected List<ContextClosedListener> closedListeners;
    protected ApplicationContext applicationContext;
    protected Map<String, String> map = new HashMap<>();
    protected Set<String> set = new HashSet<>();
    private boolean firstRunning = true;
    // protected boolean useStartLogger = true;

    @Override
    public <T> T getBean(Class<T> clazz) {
        Collection<T> beans = getBeans(clazz);
        if (beans.isEmpty())
            return null;

        T bean = null;
        for (T t : beans)
            bean = t;

        return bean;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getBean(String beanName) {
        try {
            return (T) applicationContext.getBean(getBeanName(beanName));
        } catch (NoSuchBeanDefinitionException e) {
            return null;
        }
    }

    @Override
    public <T> T getBean(String beanName, Class<T> clazz) {
        try {
            return applicationContext.getBean(getBeanName(beanName), clazz);
        } catch (NoSuchBeanDefinitionException e) {
            return null;
        }
    }

    protected String getBeanName(String beanName) {
        String dynamicBeanName = map.get(beanName);
        return dynamicBeanName == null ? beanName : dynamicBeanName;
    }

    @Override
    public <T> Collection<T> getBeans(Class<T> clazz) {
        Map<String, T> map = applicationContext.getBeansOfType(clazz);
        set.forEach(map::remove);

        return map.values();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> Class<T> getBeanClass(String beanName) {
        return (Class<T>) applicationContext.getType(beanName);
    }

    @Override
    public String[] getBeanNames() {
        return applicationContext.getBeanDefinitionNames();
    }

    @Override
    public void mapBeanName(String beanName, String dynamicBeanName) {
        set.add(beanName);
        if (map.containsKey(beanName))
            set.add(map.get(beanName));

        map.put(beanName, dynamicBeanName);
    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof ContextRefreshedEvent) {
            if (firstRunning) {
                onContextRefreshed();
                firstRunning = false;
            }
        } else if (event instanceof ContextClosedEvent)
            onContextClosed();
    }

    protected void onContextRefreshed() {
        if (Logger.isDebugEnable())
            Logger.debug("开始执行Bean环境初始化完成后续工作。");

        if (Validator.isEmpty(refreshedListeners)) {
            if (Logger.isInfoEnable())
                Logger.info("无需执行Bean环境初始化完成后续工作。");

            return;
        }

        Collections.sort(refreshedListeners, (a, b) -> a.getContextRefreshedSort() - b.getContextRefreshedSort());
        refreshedListeners.forEach(listener -> {
            listener.onContextRefreshed();
            if (Logger.isDebugEnable())
                Logger.info("正在初始第[{}]个加载器，名称是：[{}]", listener.getContextRefreshedSort(), listener.getClass().getName());
        });

        if (Logger.isInfoEnable())
            Logger.info("执行[{}]个Bean环境初始化完成后续工作。", refreshedListeners.size());
    }

    protected void onContextClosed() {
        if (Logger.isDebugEnable())
            Logger.debug("开始执行Bean环境关闭后续工作。");

        if (Validator.isEmpty(closedListeners)) {
            if (Logger.isInfoEnable())
                Logger.info("无需执行Bean环境关闭后续工作。");

            return;
        }

        Collections.sort(closedListeners, (a, b) -> a.getContextClosedSort() - b.getContextClosedSort());
        closedListeners.forEach(listener -> listener.onContextClosed());

        if (Logger.isInfoEnable())
            Logger.info("执行[{}]个Bean环境关闭后续工作。", closedListeners.size());
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
