package com.g5.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.g5.common.LeaveTypeEnum;
import com.g5.entity.AttendanceStat;
import com.g5.entity.LeaveRequest;
import com.g5.entity.vo.AttendanceStatVo;
import com.g5.mapper.AttendanceStatMapper;
import com.g5.mapper.LeaveRequestMapper;
import com.g5.service.IAttendanceStatService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 请假统计数据表 服务实现类
 * </p>
 *
 * @author Tzp
 * @since 2025-06-09
 */
@Service
public class AttendanceStatServiceImpl extends ServiceImpl<AttendanceStatMapper, AttendanceStat> implements IAttendanceStatService {

    @Autowired
    private LeaveRequestMapper leaveRequestMapper;

    /**
     * 获取学生请假统计数据
     *
     * @param days
     * @return
     */
    @Override
    public AttendanceStatVo getStatsByStudentIdAndDays(int days) {
        LocalDateTime fromTime = LocalDateTime.now().minusDays(days);
        List<LeaveRequest> list = leaveRequestMapper.selectList(
                new LambdaQueryWrapper<LeaveRequest>()
                        .ge(LeaveRequest::getCreateTime, fromTime)
                        .eq(LeaveRequest::getIsDeleted, (byte) 0)
        );

        AttendanceStatVo vo = new AttendanceStatVo();
        vo.setTotalRequests((long) list.size());
        vo.setApplyingRequests(list.stream().filter(r -> r.getStatus() == 1).count());
        vo.setApprovedRequests(list.stream().filter(r -> r.getStatus() == 2).count());
        vo.setRejectedRequests(list.stream().filter(r -> r.getStatus() == 3).count());
        vo.setCancelRequests(list.stream().filter(r -> r.getStatus() == 4).count());

        Map<Byte, Long> typeCount = list.stream()
                .collect(Collectors.groupingBy(LeaveRequest::getLeaveType, Collectors.counting()));

        Byte mostType = typeCount.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey).orElse((byte) 5); // 默认“其他”
        vo.setMostLeaveTypeName(LeaveTypeEnum.getDescByCode(mostType));

        return vo;
    }
}
