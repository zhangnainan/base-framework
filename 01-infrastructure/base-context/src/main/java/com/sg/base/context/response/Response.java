package com.sg.base.context.response;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Response
 *
 * @author Dai Wenqing
 * @date 2016/1/27
 */
public interface Response {
    /**
     * 设置类容类型。
     *
     * @param contentType 类容类型。
     */
    void setContentType(String contentType);

    /**
     * 获取输出流。
     *
     * @return 输出流。
     */
    OutputStream getOutputStream() throws IOException;

    /**
     * 跳转到指定URL地址。
     *
     * @param url 目标URL地址。
     */
    void redirectTo(String url);
}
