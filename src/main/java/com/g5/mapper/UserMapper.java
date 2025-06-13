package com.g5.mapper;

import com.g5.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 用户表（包括学生、教师、管理人员等） Mapper 接口
 * </p>
 *
 * @author Tzp
 * @since 2025-06-09
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

}
