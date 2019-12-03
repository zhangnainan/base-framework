package com.sg.base.model.support;

import com.sg.base.model.RawModel;
import com.sg.base.model.annotation.Jsonable;
import com.sg.base.model.annotation.SupperClass;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

/**
 * BaseModelExSupport
 *
 * @author Dai Wenqing
 * @date 2016/5/23
 */
@MappedSuperclass()
@SupperClass(value = "base.model..base")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class BaseModelExSupport extends RawModel implements BaseModel, Automation {
    protected int validFlag = Integer.MIN_VALUE;
    protected Date createTime;
    protected String createUser;
    protected Date modifyTime;
    protected String modifyUser;
    protected String remark;
    protected String domain;

/*
    public void initBaseField(RawModel baseModel) {
        this.validFlag = baseModel.getValidFlag();
        this.createTime = baseModel.getCreateTime();
        this.createUser = baseModel.getCreateUser();
        this.modifyTime = baseModel.getModifyTime();
        this.modifyUser = baseModel.getModifyUser();
        this.remark = baseModel.getRemark();
        this.domain = baseModel.getDomain();
    }
*/

    @Override
    @Column(name = "valid_flag")
    @Jsonable
    public int getValidFlag() {
        return validFlag;
    }

    @Override
    public void setValidFlag(int validFlag) {
        this.validFlag = validFlag;
    }

    @Override
    @Column(name = "create_time")
    @Temporal(TemporalType.TIMESTAMP)
    @Jsonable
    public Date getCreateTime() {
        return createTime;
    }

    @Override
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    @Column(name = "create_user")
    @Jsonable
    public String getCreateUser() {
        return createUser;
    }

    @Override
    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    @Override
    @Column(name = "modify_time")
    @Temporal(TemporalType.TIMESTAMP)
    @Jsonable
    public Date getModifyTime() {
        return modifyTime;
    }

    @Override
    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }

    @Override
    @Column(name = "modify_user")
    @Jsonable
    public String getModifyUser() {
        return modifyUser;
    }

    @Override
    public void setModifyUser(String modifyUser) {
        this.modifyUser = modifyUser;
    }

    @Override
    @Column(name = "remark")
    @Jsonable
    public String getRemark() {
        return remark;
    }

    @Override
    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    @Column(name = "domain")
    @Jsonable
    public String getDomain() {
        return domain;
    }

    @Override
    public void setDomain(String domain) {
        this.domain = domain;
    }
}
