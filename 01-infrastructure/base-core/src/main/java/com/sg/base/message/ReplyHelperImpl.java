package com.sg.base.message;

import com.sg.base.resource.MessageTool;
import com.sg.base.util.Validator;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * ReplyHelperImpl
 *
 * @author Dai Wenqing
 * @date 2015/11/28
 */
@Component("base.message.reply.helper")
public class ReplyHelperImpl implements ReplyHelper {

    @Autowired
    private MessageTool message;
    /*
     * @Value("${}") protected String packageInfo;
     */

   /* @Override
    public Object replyTo(success success) {
        return replyTo(success, null);
    }

    @Override
    public Object replyTo(success success, Object data) {
        return replyTo(success.getType(), data);
    }*/

    @Override
    public Object replyTo(String keyOrCodeOrMsg) {
        return replyTo(keyOrCodeOrMsg, null);
    }

    @Override
    public Object replyTo(String keyOrCodeOrMsg, Object data, Object... args) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", keyOrCodeOrMsg);

        if (data != null)
            jsonObject.put("data", data);
        if (!Validator.isEmpty(keyOrCodeOrMsg)) {
            if (args.length == 1) {
                String obj = message.get(args[0].toString());
                if (!obj.equals(args[0])) {
                    // if (packageInfo != null)
                    //obj = obj.substring(getPackagePrefix().length());
                    jsonObject.put("message", message.get(keyOrCodeOrMsg, obj));
                } else
                    jsonObject.put("message", message.get(keyOrCodeOrMsg, args));
            } else
                jsonObject.put("message", message.get(keyOrCodeOrMsg, args));
        }
        return jsonObject;
    }

    private String getPackagePrefix() {
        String packageName = this.getClass().getPackage().getName();
        String[] names = packageName.split("\\.");
        List<String> prefixs = new ArrayList<>();
        int i = 0;
        for (String name : names) {
            if (i < 2)
                prefixs.add(name);
            i++;
        }
        return String.join(".", prefixs);
    }

    @Override
    public Object replyTo(Message message, int code, Object... args) {
        return replyTo(message, null, code, args);
    }

    @Override
    public Object replyTo(Message message) {
        return replyTo(message, null, -1);
    }

    @Override
    public Object replyTo(Message message, Object data, int code, Object... args) {
        StackTraceElement stack[] = Thread.currentThread().getStackTrace();
        // String callName = stack[2].getClassName();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", message.getType());
        if (data != null)
            jsonObject.put("data", data);
        // 获取操作失败的原因

        for (StackTraceElement ste : stack) {
            // System.out.println("called by " + ste.getClassName() + "." +
            // ste.getMethodName() + "/" + ste.getFileName());
            String className = ste.getClassName().toLowerCase();
            if (className.toLowerCase().endsWith("serviceimpl") || className.toLowerCase().endsWith("ctrl")) {
                String key = "";
                String[] names = className.split("\\.");
                List<String> list = new ArrayList();
                for (String name : names) {
                    list.add(name);
                }
                // 去掉类名
                if (list.size() > 1)
                    list.remove(list.size() - 1);
                String packageName = String.join(".", list);
                // if (packageInfo != null)
                packageName = packageName.replace(getPackagePrefix(), "");
                key = packageName + ".reason" + "." + ste.getMethodName() + (code > -1 ? ("-" + code) : "");

                String replyMessage;
                if (message == Message.Error) {
                    // 获取原因
                    String reason = this.message.get(key, args);
                    if (reason.equals(key)) {
                        reason = this.message.get(Message.NoTellAnyReason.getType(), ste.getClassName(), ste.getMethodName(), ste.getLineNumber());
                    }
                    replyMessage = this.message.get(message.getType(), reason);
                } else {
                    // 获取操作对象
                    replyMessage = this.message.get(message.getType(), this.message.get(packageName));
                }
                jsonObject.put("message", replyMessage);
                if (!replyMessage.isEmpty())
                    break;
            }
        }

        return jsonObject;
    }

    @Override
    public Object replyTo(String code, String message, String parameter, String value) {
        JSONObject json = new JSONObject();
        json.put("code", code);
        json.put("message", message);
        JSONObject object = new JSONObject();
        object.put("name", parameter);
        object.put("value", value);
        json.put("parameter", object);

        return json;
    }
}
