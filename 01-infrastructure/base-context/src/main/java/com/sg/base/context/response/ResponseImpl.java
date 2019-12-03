package com.sg.base.context.response;

import com.sg.base.log.Logger;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

/**
 * ResponseImpl
 *
 * @author Dai Wenqing
 * @date 2016/1/27
 */
@Component("base.context.response")
public class ResponseImpl implements Response, HttpServletResponseAware {
    protected String servletContextPath;
    protected ThreadLocal<HttpServletResponse> responseThreadLocal = new ThreadLocal<>();

    @Override
    public void setContentType(String contentType) {
        responseThreadLocal.get().setContentType(contentType);
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        return responseThreadLocal.get().getOutputStream();
    }

    @Override
    public void redirectTo(String url) {
        try {
            responseThreadLocal.get().sendRedirect(url);
        } catch (IOException e) {
            Logger.warn(e, "跳转的URL[{}]地址时发生异常！", url);
        }
    }

    @Override
    public void set(HttpServletResponse httpServletResponse) {
        this.responseThreadLocal.set(httpServletResponse);
    }
}
