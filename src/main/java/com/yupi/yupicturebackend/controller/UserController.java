package com.yupi.yupicturebackend.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yupi.yupicturebackend.annotation.AuthCheck;
import com.yupi.yupicturebackend.common.BaseResponse;
import com.yupi.yupicturebackend.common.DeleteRequest;
import com.yupi.yupicturebackend.common.ResultUtils;
import com.yupi.yupicturebackend.constant.UserConstant;
import com.yupi.yupicturebackend.exception.BusinessException;
import com.yupi.yupicturebackend.exception.ErrorCode;
import com.yupi.yupicturebackend.exception.ThrowUtils;
import com.yupi.yupicturebackend.model.dto.user.*;
import com.yupi.yupicturebackend.model.entity.User;
import com.yupi.yupicturebackend.model.vo.LoginUserVo;
import com.yupi.yupicturebackend.model.vo.UserVo;
import com.yupi.yupicturebackend.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/user")
@Api(tags = "用户接口")
@CrossOrigin(origins = {"http://localhost:5173"}, allowCredentials = "true")
public class UserController {

    @Resource
    private UserService userService;

    /**
     * 用户注册
     *
     * @param userRegisterRequest
     * @return
     */
    @ApiOperation(value = "用户注册接口", notes = "用户通过账号密码注册，返回新用户ID")
    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        ThrowUtils.throwIf(userRegisterRequest == null, ErrorCode.PARAMS_ERROR);
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        long result = userService.userRegister(userAccount, userPassword, checkPassword);
        return ResultUtils.success(result);
    }

    /**
     * 用户登录
     *
     * @param UserLoginRequest
     * @return
     */
    @ApiOperation(value = "用户登录", notes = "用户通过账号密码登录，返回登录用户信息")
    @PostMapping("/login")
    public BaseResponse<LoginUserVo> userLogin(@RequestBody UserLoginRequest UserLoginRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(UserLoginRequest == null, ErrorCode.PARAMS_ERROR);
        String userAccount = UserLoginRequest.getUserAccount();
        String userPassword = UserLoginRequest.getUserPassword();
        LoginUserVo loginUserVo = userService.userLogin(userAccount, userPassword, request);
        return ResultUtils.success(loginUserVo);
    }

    /**
     * 获取当前登录用户
     *
     * @param request
     * @return
     */
    @ApiOperation(value = "获取当前登录用户", notes = "返回脱敏的用户信息")
    @GetMapping("/get/login")
    public BaseResponse<LoginUserVo> getLoginUser(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        // 返回脱敏的用户信息
        return ResultUtils.success(userService.getLoginUserVo(loginUser));
    }

    /**
     * 用户登录态注销
     *
     * @param request
     * @return
     */
    @PostMapping("/logout")
    @ApiOperation(value = "用户登录态注销", notes = "用户注销，返回脱敏的用户信息")
    public BaseResponse<Boolean> userLogout(HttpServletRequest request) {
        ThrowUtils.throwIf(request == null, ErrorCode.PARAMS_ERROR);
        boolean result = userService.userLogout(request);
        return ResultUtils.success(result);
    }

    /**
     * 管理员创建用户
     *
     * @param userAddRequest
     * @return
     */
    @ApiOperation(value = "管理员创建用户", notes = "返回注册的用户id")
    @PostMapping("/add")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE) // 管理员权限才可以使用此功能
    public BaseResponse<Long> addUser(@RequestBody UserAddRequest userAddRequest) {
        ThrowUtils.throwIf(userAddRequest == null, ErrorCode.PARAMS_ERROR);
        User user = new User();
        BeanUtils.copyProperties(userAddRequest, user);

        // 默认密码
        final String DEFAULT_PASSWORD = "12345678";
        String encryptPassword = userService.getEncryptPassword(DEFAULT_PASSWORD);
        user.setUserPassword(encryptPassword);

        // 插入数据库
        boolean result = userService.save(user);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(user.getId());
    }

    /**
     * 根据id获取用户未脱敏数据（仅管理员）
     *
     * @param id
     * @return
     */
    @ApiOperation(value = "根据id获取用户信息（未脱敏）", notes = "传入用户id，返回用户未脱敏数据")
    @GetMapping("/get")
    public BaseResponse<User> getUserById(long id) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        User user = userService.getById(id);
        ThrowUtils.throwIf(user == null, ErrorCode.NOT_FOUND_ERROR);
        return ResultUtils.success(user);
    }

    /**
     * 根据id获取用户信息（已脱敏）
     *
     * @param id
     * @return
     */
    @ApiOperation(value = "根据id获取用户信息（已脱敏）", notes = "传入用户id，返回用户已脱敏数据")
    @GetMapping("/get/vo")
    public BaseResponse<UserVo> getUserVoById(long id) {
        BaseResponse<User> response = getUserById(id);
        User user = response.getData();
        return ResultUtils.success(userService.getUserVo(user));
    }

    /**
     * 管理员根据id删除用户
     *
     * @param deleteRequest
     * @return
     */
    @ApiOperation(value = "管理员根据id删除用户", notes = "传入用户id，删除用户")
    @PostMapping("/delete")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deleteUser(@RequestBody DeleteRequest deleteRequest) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean b = userService.removeById(deleteRequest.getId());
        return ResultUtils.success(b);
    }

    /**
     * 管理员根据id更新用户
     *
     * @param userUpdateRequest
     * @return
     */
    @ApiOperation(value = "管理员根据id更新用户", notes = "传入用户id，更新用户信息")
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateUser(@RequestBody UserUpdateRequest userUpdateRequest) {
        if (userUpdateRequest == null || userUpdateRequest.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = new User();
        BeanUtils.copyProperties(userUpdateRequest, user);
        boolean result = userService.updateById(user);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 管理员管理用户分页
     *
     * @param userQueryRequest
     * @return
     */
    @ApiOperation(value = "管理员管理用户分页", notes = "返回分页信息")
    @PostMapping("/list/page/vo")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<UserVo>> listUserVoByPage(@RequestBody UserQueryRequest userQueryRequest) {
        ThrowUtils.throwIf(userQueryRequest == null, ErrorCode.PARAMS_ERROR);
        long current = userQueryRequest.getCurrent();
        long pageSize = userQueryRequest.getPageSize();
        // 封装分页条件，查询未脱敏的数据
        Page<User> userPage = userService.page(new Page<>(current, pageSize), userService.GetQueryWrapper(userQueryRequest));
        // 新建保存脱敏数据的Page对象
        Page<UserVo> userVoPage = new Page<>(current, pageSize, userPage.getTotal());
        // 将查询到的未脱敏数据转化为脱敏数据
        List<UserVo> userVoList = userService.getUserVoList(userPage.getRecords());
        // 将转化好的脱敏数据保存到刚才新建的Page对象中
        userVoPage.setRecords(userVoList);
        return ResultUtils.success(userVoPage);
    }
}
