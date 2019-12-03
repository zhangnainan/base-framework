/**
 *
 */
package com.sg.base.resource;

import com.sg.base.bean.BeanFactory;
import com.sg.base.bean.BeansScanRegister;
import com.sg.base.util.LocaleUtil;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

/**
 * @author lpw
 */
@Component("base.util.resource.message")
public class MessageToolImpl implements MessageTool, BeansScanRegister {
    private String localeAndCounty = "zh-cn";
    protected ResourceBundleMessageSource messageSource;

    Set<String> messages = new HashSet<>();
    PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

    @Override
    public String get(String key, Object... args) {
        return messageSource.getMessage(key, args, key, LocaleUtil.toLocale(localeAndCounty));
    }

    @Override
    public void before() {

    }

    @Override
    public void run(String beanName) {
        Package beanPackage = BeanFactory.getBean(beanName).getClass().getPackage();
        if (beanPackage == null) {
            // Logger.warn(null, "无法获得Bean包[{}]。", beanName);
            return;
        }

        String packageName = beanPackage.getName();
        if (resolver.getResource(packageName.replace('.', File.separatorChar) + "/message.properties").exists())
            messages.add(packageName);
    }

    @Override
    public void after() {
        String[] names = new String[messages.size()];
        int i = 0;
        for (String name : messages)
            names[i++] = name + ".message";
        messageSource = new ResourceBundleMessageSource();
        messageSource.setBasenames(names);

        //释放资源
        messages = null;
        resolver = null;
    }
}
