package com.sg.base.context.session;

import javax.servlet.http.HttpSession;

/**
 * HttpSessionAware
 *
 * @author Dai Wenqing
 * @date 2016/1/19
 */
public interface HttpSessionAware {
    final String error = "无法获取到httpSession变量";

    void set(HttpSession httpSession);
}
