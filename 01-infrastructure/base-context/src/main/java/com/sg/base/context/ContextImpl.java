package com.sg.base.context;

import com.sg.base.log.Logger;
import com.sg.base.util.Validator;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author lpw
 */
@Component("snow.context")
public class ContextImpl implements Context {
    protected Map<String, String> map = new ConcurrentHashMap<>();
    protected String root;
    protected ThreadLocal<Locale> locale = new ThreadLocal<>();
    protected static final String ROOT = "/";
    protected int contextPathCount;
    protected String servletContextPath;
    protected String contextPath;

    @Override
    public void setPath(String root, String contextPath) {
        this.root = root;
        this.contextPath = contextPath;
        map.clear();

        contextPathCount = Validator.isEmpty(contextPath) || contextPath.equals(ROOT) ? 0 : contextPath.length();
        servletContextPath = contextPathCount > 0 ? contextPath : "";

        if (Logger.isInfoEnable())
            Logger.info("设置运行期根路径：{}", root);
    }

    @Override
    public String getAbsolutePath(String path) {
        if (map.containsKey(path))
            return map.get(path);

        if (contextPathCount > 0)
            path = path.substring(contextPathCount);

        File file = new File(root + "/" + path);
        String absolutePath = "";
        if (file.exists())
            absolutePath = file.getAbsolutePath();
        else {
            absolutePath = file.getAbsolutePath() + "/WEB-INF/";
        }
        map.put(path, absolutePath);

        return absolutePath;
    }

    @Override
    public void setLocale(Locale locale) {
        this.locale.set(locale);
    }

    @Override
    public Locale getLocale() {
        Locale locale = this.locale.get();
        if (locale == null)
            locale = Locale.getDefault();

        return locale;
    }
}
