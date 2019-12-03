package com.sg.base.message;

/**
 * Reply
 *
 * @author Dai Wenqing
 * @date 2015/12/2
 */
public interface Reply {
    /**
     * 封装执行成功返回结果说明。
     *
     * @param data 数据。
     * @return 执行成功返回结果说明。
     */
    Object success(Object data);

    Object success();

    /**
     * 返回成功的标识代码,OK
     *
     * @return
     */
/*    Object ok();

    Object ok(Object data);*/

    Object failure(Message message, Object data);

    Object failure(Message message, Object data, Object... args);

    Object failure(Message message);

    Object failure(int code);

    Object failure(int code, Object... args);

    Object failure();
}
