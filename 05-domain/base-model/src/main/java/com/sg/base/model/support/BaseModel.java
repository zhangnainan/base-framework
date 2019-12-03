package com.sg.base.model.support;

import com.sg.base.model.Model;

import java.util.Date;

/**
 * BaseModel
 *
 * @author Dai Wenqing
 * @date 2016/1/7
 */
public interface BaseModel extends Model {
    /**
     * 获取标志。
     *
     * @return 标志。
     */
    int getValidFlag();

    /**
     * 设置标志。 0 = 表示有效
     *
     * @param validFlag 标志。
     */
    void setValidFlag(int validFlag);

    /**
     * 获取创建时间。
     *
     * @return 创建时间。
     */
    Date getCreateTime();

    /**
     * 设置创建时间。
     *
     * @param createTime 创建时间。
     */
    void setCreateTime(Date createTime);

    /**
     * 获取创建人。
     *
     * @return 创建人。
     */
    String getCreateUser();

    /**
     * 设置创建人。
     *
     * @param createUser 创建人。
     */
    void setCreateUser(String createUser);

    /**
     * 获取修改时间。
     *
     * @return 修改时间。
     */
    Date getModifyTime();

    /**
     * 设置修改时间。
     *
     * @param modifyTime 修改时间。
     */
    void setModifyTime(Date modifyTime);

    /**
     * 获取修改用户。
     *
     * @return 修改用户。
     */
    String getModifyUser();

    /**
     * 设置修改用户。
     *
     * @param modifyUser 修改用户。
     */
    void setModifyUser(String modifyUser);

    /**
     * 获取备注。
     *
     * @return 备注。
     */
    String getRemark();

    /**
     * 设置备注。
     *
     * @param remark 备注。
     */
    void setRemark(String remark);

    /**
     * 获取域。
     *
     * @return 域。
     */
    String getDomain();

    /**
     * 设置域。
     *
     * @param domain 域。
     */
    void setDomain(String domain);


}
