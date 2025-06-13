package com.g5.service;

import com.g5.entity.AttendanceStat;
import com.baomidou.mybatisplus.extension.service.IService;
import com.g5.entity.vo.AttendanceStatVo;

/**
 * <p>
 * 请假统计数据表 服务类
 * </p>
 *
 * @author Tzp
 * @since 2025-06-09
 */

public interface IAttendanceStatService extends IService<AttendanceStat> {
    /**
     * 获取学生请假统计数据
     * @param days
     * @return AttendanceStatVo
     */
    AttendanceStatVo getStatsByStudentIdAndDays( int days);
}
