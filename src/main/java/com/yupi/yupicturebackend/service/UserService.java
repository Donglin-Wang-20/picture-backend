package com.yupi.yupicturebackend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yupi.yupicturebackend.model.dto.user.UserQueryRequest;
import com.yupi.yupicturebackend.model.entity.User;
import com.yupi.yupicturebackend.model.vo.LoginUserVo;
import com.yupi.yupicturebackend.model.vo.UserVo;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author Administrator
 * @description 针对表【user(用户)】的数据库操作Service
 * @createDate 2025-05-01 20:55:09
 */
public interface UserService extends IService<User> {

    /**
     * 用户注册
     *
     * @param userAccount   用户账户
     * @param userPassword  用户密码
     * @param checkPassword 校验密码
     * @return 新用户 id
     */
    long userRegister(String userAccount, String userPassword, String checkPassword);

    /**
     * 用户登录
     *
     * @param userAccount  用户账户
     * @param userPassword 用户密码
     * @param request
     * @return 脱敏后的用户信息
     */
    LoginUserVo userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 获取加密后的密码
     *
     * @param userPassword
     * @return
     */
    String getEncryptPassword(String userPassword);

    /**
     * 获取当前登录用户
     *
     * @param request
     * @return
     */
    User getLoginUser(HttpServletRequest request);

    /**
     * 获得脱敏后的登录用户信息
     *
     * @param user
     * @return
     */
    LoginUserVo getLoginUserVo(User user);

    /**
     * 获得脱敏后的用户信息
     *
     * @param user
     * @return
     */
    UserVo getUserVo(User user);

    /**
     * 获得脱敏后的用户信息列表
     *
     * @param users
     * @return 脱敏后的用户列表
     */
    List<UserVo> getUserVoList(List<User> users);

    /**
     * 用户登录态注销
     *
     * @param request
     * @return
     */
    boolean userLogout(HttpServletRequest request);

    /**
     * 获取查询条件
     *
     * @param userQueryRequest
     * @return
     */
    QueryWrapper<User> GetQueryWrapper(UserQueryRequest userQueryRequest);

    /**
     * 是否为管理员
     *
     * @param user
     * @return
     */
    boolean isAdmin(User user);
}
