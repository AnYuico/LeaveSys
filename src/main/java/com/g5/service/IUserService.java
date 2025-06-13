package com.g5.service;

import com.g5.common.Result;
import com.g5.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 用户表（包括学生、教师、管理人员等） 服务类
 * </p>
 *
 * @author Tzp
 * @since 2025-06-09
 */
public interface IUserService extends IService<User> {


    Result<User> login(String username, String password);

    Result<User> register(String username, String password, String realName, String role);
}
