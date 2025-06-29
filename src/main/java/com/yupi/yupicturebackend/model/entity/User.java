package com.yupi.yupicturebackend.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

/**
 * 用户
 *
 * @TableName user
 */
@TableName(value = "user")
@Data
public class User {
    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID) // 生成长整型 long类型的id
    private Long id;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 密码
     */
    private String userPassword;

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 用户简介
     */
    private String userProfile;

    /**
     * 用户角色：user/admin
     */
    private String userRole;

    /**
     * 编辑时间
     */
    private Date editTime;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 是否删除
     */
    @TableLogic // 逻辑删除注解
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}