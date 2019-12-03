package com.sg.base.context.validator;


import com.sg.base.bean.BeanFactory;
import com.sg.base.message.Message;
import com.sg.base.message.Reply;

/**
 * Checker
 *
 * @author Dai Wenqing
 * @date 2015/12/7
 */
public interface Checker {

    /**
     * 输出错误消息
     *
     * @param message 错误代码
     * @param args    代码参数
     * @return 错误消息
     */
    default Object failure(Message message, Object... args) {
        return BeanFactory.getBean(Reply.class).failure(message, null, args);
    }

    /**
     * 验证
     *
     * @param value     参数值
     * @param paramters 参数
     * @return
     */
    boolean validate(String value, String[] paramters);
}
