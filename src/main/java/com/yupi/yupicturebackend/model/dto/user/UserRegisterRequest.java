package com.yupi.yupicturebackend.model.dto.user;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserRegisterRequest implements Serializable {

    /**
     * 保证序列化和反序列化时类的版本一致，避免反序列化异常
     */
    private static final long serialVersionUID = 8735650154179439661L;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 密码
     */
    private String userPassword;

    /**
     * 确认密码
     */
    private String checkPassword;
}
