package com.g5.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.g5.common.Result;
import com.g5.entity.User;
import com.g5.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 用户表（包括学生、教师、管理人员等） 前端控制器
 * </p>
 *
 * @author Tzp
 * @since 2025-06-09
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private IUserService userService;

    /**
     * 用户登录接口
     *
     * @param username 用户名
     * @param password 密码
     * @return 登录结果
     */
    @PostMapping("/login")
    public Result login(@RequestParam String username, @RequestParam String password) {
        System.out.println("进入Login方法"+username+password);
        try {
            Result<User> result = userService.login(username, password);
            User user = result.getData();
            if (user==null){
                System.out.println("用户名或密码错误");
                return Result.error("用户名或密码错误");
            }else {
                StpUtil.login(user.getUserId());
                return Result.success(user);
            }
        } catch (Exception e) {
            System.out.println("登录异常");
            throw new RuntimeException(e);
        }
    }

    /**
     * 用户注册接口
     *
     * @param username 用户名
     * @param password 密码
     * @return 注册结果
     */
    @PostMapping("/register")
    public Result register(@RequestParam String username, @RequestParam String password,
                           @RequestParam String realName,@RequestParam String role) {
        System.out.println("进入Register方法"+username+password);
        try {
            Result<User> result = userService.register(username, password, realName,role);
            User user = result.getData();
            if (user == null) {
                System.out.println("用户名已存在");
                return Result.error("用户名已存在");
            } else {
                return Result.success(user);
            }
        } catch (Exception e) {
            System.out.println("注册异常");
            throw new RuntimeException(e);
        }
    }

    /**
     * 根据用户ID查询用户信息
     *
     * @param userId 用户ID
     * @return 用户信息
     */
    @PostMapping("/getUserById")
    public Result getUserById(@RequestParam Long userId) {
        try {
            User user = userService.getById(userId);
            if (user == null) {
                return Result.error("用户不存在");
            }
            return Result.success(user);
        } catch (Exception e) {
            throw new RuntimeException("查询用户信息异常", e);
        }
    }
}
