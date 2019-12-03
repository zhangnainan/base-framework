package com.sg.base.context.request;

import com.sg.base.bean.BeanFactory;
import com.sg.base.log.Logger;
import com.sg.base.model.Model;
import com.sg.base.model.ModelHelper;
import com.sg.base.resource.Io;
import com.sg.base.util.Converter;
import com.sg.base.util.Security;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * RequestImpl
 *
 * @author Dai Wenqing
 * @date 2016/1/18
 */
@Component("base.context.http-request")
public class RequestImpl implements Request, HttpServletRequestAware {
    private static final String SIGN = "sign";
    // Session session;
    private ThreadLocal<HttpServletRequest> requestThreadLocal = new ThreadLocal<>();
    private Map<String, String> map = null;
    private String content;
    @Value("${snow.context.request.sign.key}")
    private String key = "snow";

    @Override
    public void setHttpServletRequest(HttpServletRequest httpServletRequest) {
        this.requestThreadLocal.set(httpServletRequest);
    }

    @Override
    public String get(String name) {
        String value = requestThreadLocal.get().getParameter(name);
        if (value != null)
            return value;

        if (map == null) {
            map = new HashMap<>();
            StringBuilder sb = new StringBuilder(getFromInputStream());
            value = "";
            for (int indexOf; (indexOf = sb.lastIndexOf("&")) > -1; ) {
                String string = sb.substring(indexOf + 1);
                sb.delete(indexOf, sb.length());
                if ((indexOf = string.indexOf('=')) == -1) {
                    value = "&" + string + value;

                    continue;
                }
                map.put(string.substring(0, indexOf), Converter.decodeUrl(string.substring(indexOf + 1) + value, null));
                value = "";
            }
        }

        return map.get(name);
    }

    @Override
    public int getAsInt(String name) {
        return Converter.toInt(get(name));
    }

    @Override
    public long getAsLong(String name) {
        return Converter.toLong(get(name));
    }

    @Override
    public Date getAsDate(String name) {
        return Converter.toDate(get(name));
    }

    @Override
    public Map<String, String> getMap() {
        Map<String, String> map = new HashMap<>();
        Map<String, String[]> parameters = requestThreadLocal.get().getParameterMap();
        parameters.forEach((key, value) -> map.put(key, value[0]));

        return map;
    }

    @Override
    public String getFromInputStream() {
        if (content != null)
            return content;

        try {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            BeanFactory.getBean(Io.class).copy(requestThreadLocal.get().getInputStream(), output);
            output.close();
            content = output.toString();

            return content;
        } catch (IOException e) {
            Logger.warn(e, "获取InputStream中的数据时发生异常！");

            return "";
        }
    }

    @Override
    public <T extends Model> void setToModel(T model) {
        if (model == null)
            return;
        JSONObject jsonObject = new JSONObject();
        for (Method method : model.getClass().getMethods()) {
            if (method.getName().startsWith("get")) {
                String name = method.getName().replace("get", "");
                name = name.substring(0, 1).toLowerCase() + name.substring(1);
                jsonObject.put(name, get(name));
            }
        }
        model = ModelHelper.fromJson(jsonObject, (Class<T>) model.getClass());
    }

    @Override
    public boolean checkSign() {
        Map<String, String> map = getMap();
        if (map.get(SIGN) == null)
            return false;

        List<String> list = new ArrayList<>();
        list.addAll(getMap().keySet());
        list.remove(SIGN);
        Collections.sort(list);

        StringBuilder sb = new StringBuilder();
        for (String key : list)
            sb.append(key).append('=').append(map.get(key)).append('&');
        sb.append(key);

        return Security.md5(sb.toString()).equals(map.get(SIGN));
    }

    @Override
    public String getServerName() {
        return requestThreadLocal.get().getServerName();
    }

    @Override
    public int getServerPort() {
        return requestThreadLocal.get().getServerPort();
    }

    @Override
    public String getContextPath() {
        return requestThreadLocal.get().getContextPath();
    }

    @Override
    public String getRealPath(String path) {
        return requestThreadLocal.get().getSession().getServletContext().getRealPath(path);
    }

    @Override
    public String getUri() {
        return requestThreadLocal.get().getRequestURI();
    }

    @Override
    public String getMethod() {
        return requestThreadLocal.get().getMethod();
    }

    @Override
    public String getHeader(String key) {
        if (requestThreadLocal != null)
            return requestThreadLocal.get().getHeader(key);
        return "";
    }

    @Override
    public String getIp() {
        if (requestThreadLocal != null) {
            if (requestThreadLocal.get().getHeader("x-forwarded-for") == null) {
                return requestThreadLocal.get().getRemoteAddr();
            }
            return requestThreadLocal.get().getHeader("x-forwarded-for");
        }
        return "";
    }

    @Override
    public Map<String, String> getHeaderMap() {
        Map<String, String> map = new HashMap<String, String>();
        for (Enumeration<String> names = requestThreadLocal.get().getHeaderNames(); names.hasMoreElements(); ) {
            String name = names.nextElement();
            map.put(name, requestThreadLocal.get().getHeader(name));
        }

        return map;
    }
}
