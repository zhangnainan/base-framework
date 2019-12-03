package com.sg.base.message;

/**
 * Message
 *
 * @author Dai Wenqing
 * @date 2015/11/28
 */
public enum Message {

    /**
     * 成功操作
     */
    Success("success"),

    /**
     * 业务逻辑相关
     */
    Business("business"),
    /**
     * 通用错误提醒，可追加原因
     */
    Error("error"),

    /* 调用服务失败 */
    ServiceError("error.service"),

    /**
     * 非法输入
     */
    InputIllegal("error.input.illegal"),

    /**
     * 信息已经存在
     */
    Exist("error.exists"),

    NoExist("error.not.exists"),

    /**
     * 添加失败
     */
    AddError("error.add"),

    /**
     * 更新失败
     */
    UpdateError("error.update"),

    /**
     * 删除失败
     */
    DeleteError("error.delete"),

    /**
     * 无法查询到相关记录
     */
    SelectNoAnyRecord("error.select.not.record"),

    /**
     * 重复操作
     */
    RepeatDoWork("error.repeat.dowork"),

    IllegalOperate("error.illegal.operate"),

    NoTellAnyReason("error.not.tell.any.reason"),

    /**
     * 未授权
     */
    UnAuthorized("error.unauthorized"),
    /**
     * 不允许为空
     */
    NotEmpty("error.parameter.not.empty"),
    /**
     * 超过最大长度
     */
    OverMaxLength("error.parameter.over.max.length"),
    /**
     * 长度最小长度
     */
    LessMinLength("error.parameter.less.min.length"),

    /**
     * 规则不匹配
     */
    NotMatchRegex("error.parameter.not.match.regex"),
    /**
     * 邮件格式非法
     */
    IllegalEmail("error.parameter.illegal.email"),
    /**
     * 不允许等于某个对象值
     */
    NotEqual("error.parameter.not.equal"),
    /**
     * 未等于某个对象或值
     */
    Equal("error.parameter.equal"),
    /**
     * 不大于
     */
    NotGreaterThan("error.parameter.not.greater.than"),

    /* 当前位置的参数为空 */
    ParameterPositionInHolderIsnull("error.parameter.position-in-holder-is-null"),

    /**
     * 不小于
     */
    NotLessThan("error.parameter.less.than"),
    /**
     * 不在区间内
     */
    NotBetween("error.parameter.not.between"),

    Busy("error.system.dispatch.busy"),

    LoginLock("error.login.lock"),

    ExistsRecycle("error.exists.recycle"),

    ExistsRecycleFailure("error.exists.recycle.failure"),

    CheckerNotFound("error.checker.not.find"),

    LoginError("error.login.error");

    private String type;
    private String[] args;

    public Message setArgs(String... args) {
        this.args = args;
        return this;
    }

    public String[] getArgs() {
        return this.args;
    }

    Message(String type) {
        this.type = type;
        this.args = args;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
