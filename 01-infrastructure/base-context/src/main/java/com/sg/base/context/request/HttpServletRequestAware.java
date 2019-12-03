package com.sg.base.context.request;

import javax.servlet.http.HttpServletRequest;

/**
 * HttpServletRequestAware
 *
 * @author Dai Wenqing
 * @date 2016/1/19
 */
public interface HttpServletRequestAware {
    void setHttpServletRequest(HttpServletRequest httpServletRequest);
}
