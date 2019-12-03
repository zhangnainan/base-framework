package com.sg.base.message;

/**
 * ReplyHelper 消息应答
 *
 * @author Dai Wenqing
 * @date 2015/11/28
 */
public interface ReplyHelper {
    /**
     * 封装对成功消息的应答
     *
     * @param success 成功代码
     * @return 成功应答
     */
    //Object replyTo(success success);

    /**
     * 封装对成功消息的应答
     *
     * @param success 成功代码
     * @param data    返回成功的数据
     * @return 成功应答
     */
    //Object replyTo(success success, Object data);

    /**
     * 封装自定义消息的应答
     *
     * @param keyOrCodeOrMsg 自定义关键字或代码
     * @return 自定义应答
     */
    Object replyTo(String keyOrCodeOrMsg);

    /**
     * 封装对自定义消息的应答
     *
     * @param keyOrCodeOrMsg 自定义关键字或代码
     * @param data           要返回的数据
     * @param args           消息可以替代的参数
     * @return 自定义应答
     */
    Object replyTo(String keyOrCodeOrMsg, Object data, Object... args);

    /**
     * 返回失败的消息应答
     *
     * @param message 失败代码
     * @return 失败应答
     */
    Object replyTo(Message message);

    Object replyTo(Message message, int code, Object... args);

    /**
     * 返回失败的消息应答
     *
     * @param message 失败代码
     * @param data    失败的附加数据
     * @return 失败应答
     */
    Object replyTo(Message message, Object data, int code, Object... args);

    /**
     * 封装验证失败结果信息。
     *
     * @param code      验证失败编码。
     * @param message   验证失败错误信息。
     * @param parameter 验证参数名。
     * @param value     验证参数值。
     * @return 失败结果信息。
     */
    Object replyTo(String code, String message, String parameter, String value);

}
