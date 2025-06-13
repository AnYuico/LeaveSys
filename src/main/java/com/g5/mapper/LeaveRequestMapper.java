package com.g5.mapper;

import com.g5.entity.LeaveRequest;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 请假申请表 Mapper 接口
 * </p>
 *
 * @author Tzp
 * @since 2025-06-09
 */
@Mapper
public interface LeaveRequestMapper extends BaseMapper<LeaveRequest> {

    @Select("SELECT * FROM leave_request WHERE create_time >= #{fromTime} AND is_deleted = 0")
    List<LeaveRequest> selectByCreateTimeAfter(LocalDateTime fromTime);

}
