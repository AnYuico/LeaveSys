package com.g5.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.g5.common.Result;
import com.g5.entity.User;
import com.g5.entity.dto.RegisterDTO;
import com.g5.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

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
     * 登录
     * @param params
     * @return
     */
    @PostMapping("/login")
    public Result login(@RequestBody Map<String, String> params) {
        String username = params.get("username");
        String password = params.get("password");

        Result<User> result = userService.login(username, password);

        if (result.getData() != null) {
            StpUtil.login(result.getData().getUserId());
            Map<String, Object> data = new HashMap<>();
            data.put("user", result.getData());
            data.put("token", StpUtil.getTokenValue());
            return Result.success(data);
        }
        return result; // 返回错误信息
    }


    /**
     * 注册
     * @param registerDTO
     * @return
     */
    @PostMapping("/register")
    public Result<User> register(@RequestBody RegisterDTO registerDTO) {
        System.out.println("进入Register方法: " + registerDTO.getUsername());
        System.out.println(registerDTO.toString());
        try {
            Result<User> result = userService.register(
                    registerDTO.getUsername(),
                    registerDTO.getPassword(),
                    registerDTO.getRealName(),
                    registerDTO.getRole()
            );

            User user = result.getData();
            if (user == null) {
                return Result.error("用户名已存在");
            } else {
                return Result.success(user);
            }
        } catch (Exception e) {
            System.out.println("注册异常");
            return Result.error("注册异常，请重试");
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
