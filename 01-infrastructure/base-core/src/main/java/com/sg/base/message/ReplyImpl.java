package com.sg.base.message;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * ReplyImpl
 *
 * @author Dai Wenqing
 * @date 2015/12/2
 */
@Component("base.message.reply")
public class ReplyImpl implements Reply {
    @Autowired
    private ReplyHelper replyHelper;

    /**
     * 封装执行成功返回结果说明。
     *
     * @param data 数据。
     * @return 执行成功返回结果说明。
     */
    @Override
    public Object success(Object data) {
        return replyHelper.replyTo(Message.Success.getType(), data);
    }

    @Override
    public Object success() {
        return replyHelper.replyTo(Message.Success.getType());
    }

    /**
     * 返回成功的标识代码,OK
     *
     * @return
     */
/*    @Override
    public Object ok() {
        return replyHelper.replyTo(success.OK);
    }

    @Override
    public Object ok(Object data) {
        return replyHelper.replyTo(success.OK, data);
    }*/
    @Override
    public Object failure(Message message, Object data) {
        return replyHelper.replyTo(message, data, -1);
    }

    @Override
    public Object failure(Message message, Object data, Object... args) {
        return replyHelper.replyTo(message.getType(), data, args);
    }

    @Override
    public Object failure(int code) {
        return replyHelper.replyTo(Message.Error, null, code);
    }

    @Override
    public Object failure(int code, Object... args) {
        return replyHelper.replyTo(Message.Error, null, code, args);
    }

    @Override
    public Object failure(Message message) {
        return replyHelper.replyTo(message);
    }

    @Override
    public Object failure() {
        return replyHelper.replyTo(Message.Error);
    }
}
