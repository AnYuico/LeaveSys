package com.g5.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.g5.common.Result;
import com.g5.entity.User;
import com.g5.mapper.UserMapper;
import com.g5.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户表（包括学生、教师、管理人员等） 服务实现类
 * </p>
 *
 * @author Tzp
 * @since 2025-06-09
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Autowired
    private UserMapper userMapper;
    @Override
    public Result<User> login(String username, String password) {
        // 根据用户名查询用户
        User user = userMapper.selectOne(new QueryWrapper<User>().eq("username", username));
        // 验证用户是否存在且密码匹配
        if (user != null && user.getPassword().equals(password)) {
            return Result.success(user);
        }
        // 用户不存在或密码错误
        return Result.error("用户名或密码错误");
    }

    @Override
    public Result<User> register(String username, String password, String realName, String role) {
        // 检查用户名是否已存在
        User existingUser = userMapper.selectOne(new QueryWrapper<User>().eq("username", username));
        if (existingUser != null) {
            return Result.error("用户名已存在");
        }

        // 创建新用户
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(password);
        newUser.setRealName(realName);
        newUser.setRole(Byte.valueOf(role));

        // 保存用户
        int result = userMapper.insert(newUser);
        if (result > 0) {
            return Result.success(newUser);
        }
        return Result.error("注册失败");
    }

}
