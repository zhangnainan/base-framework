package com.sg.base.web;

import com.sg.base.bean.BeanFactory;
import com.sg.base.conf.Configuration;
import com.sg.base.context.Context;
import com.sg.base.context.request.HttpServletRequestAware;
import com.sg.base.context.request.Request;
import com.sg.base.context.response.HttpServletResponseAware;
import com.sg.base.context.response.Response;
import com.sg.base.context.session.HttpSessionAware;
import com.sg.base.context.session.Session;
import com.sg.base.context.session.SessionAdapter;
import com.sg.base.context.session.SessionAdapterAware;
import com.sg.base.log.Logger;
import com.sg.base.util.Converter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

/**
 * ServiceFilter
 *
 * @author Dai Wenqing
 * @date 2016/1/19
 */
public class ServiceFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // this.context.setRoot(filterConfig.getServletContext().getRealPath(""));
        setPath(filterConfig.getServletContext().getRealPath(""), filterConfig.getServletContext().getContextPath());
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        setContext((HttpServletRequest) request, (HttpServletResponse) response);
        if (!ignore((HttpServletRequest) request, (HttpServletResponse) response))
            chain.doFilter(request, response);
    }

    @Override
    public void destroy() {

    }

    /**
     * 忽略文件未日期未改的文件，减少重复请求
     *
     * @param request
     * @param response
     */
    public boolean ignore(HttpServletRequest request, HttpServletResponse response) {
        Context context = BeanFactory.getBean(Context.class);
        String uri = request.getRequestURI();

        File file = new File(context.getAbsolutePath(uri));
        /*if (!file.exists())
            return false;*/

        long time = request.getDateHeader("If-Modified-Since");
        if (time > 0) {
            if (time >= file.lastModified()) {
                response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
                if (Logger.isDebugEnable())
                    Logger.debug("请求的资源[{}]已是最新的，返回[304]状态。", uri);
                return true;
            }
        }
        response.addDateHeader("Last-Modified", file.lastModified());
        response.setHeader("ETag", Converter.toString(file.lastModified()));
        /*if (Logger.isDebugEnable())
            Logger.debug("请求资源[{}]已变更，重新获取最新资源。", uri);*/
        return false;
    }

    private void setPath(String real, String contextPath) {
        Context context = BeanFactory.getBean(Context.class);
        context.setPath(real, contextPath);
        if (Logger.isInfoEnable())
            Logger.info("部署项目路径[{}]。", context);
    }

    /**
     * 设置上下文
     *
     * @param request
     * @param response
     */
    private void setContext(HttpServletRequest request, HttpServletResponse response) {
        Request r = BeanFactory.getBean(Request.class);
        ((HttpServletRequestAware) r).setHttpServletRequest(request);

        Configuration configuration = BeanFactory.getBean(Configuration.class);
        SessionAdapter sessionAdapter = BeanFactory.getBean("snow.context.session." + configuration.getSessionName());
        ((HttpSessionAware) sessionAdapter).set(request.getSession());

        Session session = BeanFactory.getBean(Session.class);
        ((SessionAdapterAware) session).setSession(sessionAdapter);

        Response rp = BeanFactory.getBean(Response.class);
        ((HttpServletResponseAware) rp).set(response);
    }
}
