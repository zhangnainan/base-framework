package com.sg.base.crud;

import com.sg.base.bean.BeanFactory;
import com.sg.base.crud.exception.ExistsException;
import com.sg.base.crud.exception.ExistsInRecycleBinException;
import com.sg.base.log.Logger;
import com.sg.base.message.Message;
import com.sg.base.model.Model;
import com.sg.base.model.ModelHelper;
import com.sg.base.model.PageList;
import com.sg.base.resource.Callable;
import com.sg.base.resource.MessageTool;
import com.sg.base.util.Validator;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 返回的结果集
 *
 * @author Dai Wenqing
 * @date 2016/3/4
 */
@Service("base.crud.result")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class Result<T> implements Serializable {
    private T data;
    // @JsonProperty
    private String message;
    // @JsonProperty
    private String code;

    public Result(T data, Message message) {
        this.data = data;
        this.message = message.getType();
    }

    public Result() {
    }

    public T getData() {
        return data;
    }

    /*
     * public void setData(T data) { this.data = data; }
     */

    public String getMessage() {
        return message;
    }

    /*
     * public void setMessage(String message) { this.message = message; }
     */

    public String getCode() {
        return code;
    }

    /*
     * public void setCode(String code) { this.code = code; }
     */

    public Object toJson() {
        if (data instanceof Model || data == null || data instanceof PageList) {
            Map<String, Object> map = new HashMap<>();
            // JSONObject jsonObject = new JSONObject();
            if (data instanceof Model) {
                map.put("data", ModelHelper.toJson((Model) data));
            } else if (data instanceof PageList) {
                PageList pageList = (PageList) data;
                map.put("data", pageList.toJson());
            }
            map.put("message", this.getMessage());
            map.put("code", this.getCode());
            return map;
        }
        return JSONObject.fromObject(this);
    }


    /**
     * 结果响应：
     * 1、对可执行的方法进行封装， 最后返回统一的格式；
     * 2、对异常进行统一处理；
     * 3、对重复数据进行判断，但需要在实体字段上增加注释@Unique
     * 4、对返回的结果及消息进行封装格式如果下：
     * {code:'结果代码',message:'结果消息',data'结果数据'}
     *
     * @param callable 可执行方法
     * @param args
     * @param <T>
     * @return
     */
    public static <T> Result reply(Callable<T> callable, String... args) {
        T data = null;
        Result result = BeanFactory.getBean(Result.class);
        try {
            data = callable.call();
        } catch (ExistsException e) {
            // 数据已经存在
            result.setResult(data, Message.Exist, e.getCode());
            return result;
        } catch (ExistsInRecycleBinException e) {
            result.setResult(data, Message.ExistsRecycle, e.getCode());
            try {
                // 数据已经存在收回站，系统尝试自动回收数据
                CrudService crudService = BeanFactory.getBean(CrudService.class);
                crudService.execute().recycle((Class<Model>) data.getClass(), Model.class.cast(data).getId());
            } catch (Exception ex) {
                result.setResult(data, Message.ExistsRecycleFailure, args);
            }
            return result;
        } catch (Exception e) {
            result.setResult(null, Message.ServiceError, args);
            Logger.error(e, "执行服务时发生了异常!");
            return result;
        }
        if (Validator.isEmpty(data)) {
            result.setResult(null, Message.NoExist, args);
        } else {
            if (data instanceof Model) {
                result.setResult(data, Message.Success, args);
            } else if (data instanceof PageList) {
                if (PageList.class.cast(data).getList().size() > 0)
                    result.setResult(data, Message.Success, args);
                else
                    result.setResult(data, Message.SelectNoAnyRecord, args);
            } else if (data instanceof JSONArray) {
                if (JSONArray.class.cast(data).size() > 0)
                    result.setResult(data, Message.Success, args);
                else
                    result.setResult(data, Message.SelectNoAnyRecord, args);
            } else if (data instanceof Message) {
                Message message = Message.class.cast(data);
                if (!Validator.isEmpty(message.getArgs()))
                    result.setResult(data, message, message.getArgs());
                else
                    result.setResult(data, message, args);
            } else {
                if (data.toString().toLowerCase().equals("true") || data.toString().toLowerCase().equals("false")) {
                    boolean r = Boolean.valueOf(data.toString());
                    if (r)
                        result.setResult(true, Message.Success, args);
                    else
                        result.setResult(false, Message.Error, args);
                } else
                    result.setResult(data, Message.Success, args);
            }
        }
        return result;
    }

    public void setResult(T data, Message code, String... args) {
        MessageTool messageTool = BeanFactory.getBean(MessageTool.class);

        Class<?> classZ = null;
        if (!Validator.isEmpty(data)) {
            classZ = data.getClass();
        }

        String packageName;
        if (classZ == null)
            packageName = this.getClass().getGenericSuperclass().getTypeName();
        else
            packageName = classZ.getTypeName();

        String[] packages = packageName.split("\\.");

        List<String> lastPackageNameList = new ArrayList<>();

        for (int i = 0; i < packages.length; i++) {
            if (i > 1 && i < packages.length - 1)
                lastPackageNameList.add(packages[i]);
        }

        String lastPackageName = String.join(".", lastPackageNameList);
        String objName = messageTool.get(lastPackageName);
        List<String> lastArgs = new ArrayList<>();
        if (!objName.equals(lastPackageName))
            lastArgs.add(objName);
        if (!Validator.isEmpty(args)) {
            if (args.length > 0) {
                for (String reason : args) {
                    lastArgs.add(messageTool.get(reason));
                }
            }
        }
        String msg = "";
        if (!Validator.isEmpty(code))
            msg = messageTool.get(code.getType(), lastArgs.toArray());
        this.data = data;
        if (!Validator.isEmpty(code))
            this.code = code.getType();
        this.message = msg;
    }

    /*
     * public void setResult(T data, Message code, String... reasons) {
     * setResult(data, code, null, reasons); }
     */
}
