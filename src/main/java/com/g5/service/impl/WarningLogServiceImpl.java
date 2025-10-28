package com.g5.service.impl;

import cn.dev33.satoken.annotation.SaCheckRole;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.g5.entity.LeaveRequest;
import com.g5.entity.User;
import com.g5.entity.WarningLog;
import com.g5.entity.vo.WarningLogVo;
import com.g5.mapper.LeaveRequestMapper;
import com.g5.mapper.UserMapper;
import com.g5.mapper.WarningLogMapper;
import com.g5.service.IWarningLogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 请假异常预警记录表 服务实现类
 * </p>
 *
 * @author Tzp
 * @since 2025-06-09
 */
@SaCheckRole("2")
@Service
public class WarningLogServiceImpl extends ServiceImpl<WarningLogMapper, WarningLog> implements IWarningLogService {

    @Autowired
    private WarningLogMapper warningLogMapper;

    @Autowired
    private LeaveRequestMapper leaveRequestMapper;

    @Autowired
    private UserMapper userMapper;

    /**
     * 获取当前预警记录
     * @return
     */
    @Override
    public List<WarningLogVo> getCurrentWarnings() {
        // 查询所有未处理的预警记录
        List<WarningLog> warnings = warningLogMapper.selectList(
                new QueryWrapper<WarningLog>().eq("handled", 0)
        );

        // 转换成VO，同时填充学生信息
        List<WarningLogVo> result = warnings.stream().map(warning -> {
            WarningLogVo vo = new WarningLogVo();
            BeanUtils.copyProperties(warning, vo);
            vo.setWarningId(warning.getWarningLogId());
            vo.setWarningTime(warning.getCreateTime());
            vo.setDescription(warning.getMessage());
            vo.setIsHandled(warning.getHandled());
            // 根据 studentId 查询学生信息
            User userStudent = userMapper.selectById(warning.getUserId());
            if (userStudent != null) {
                vo.setStudentId(userStudent.getUserId());
                vo.setStudentName(userStudent.getRealName());
                vo.setStudentNumber(userStudent.getUserId().toString());
            }
            return vo;
        }).collect(Collectors.toList());

        return result;
    }

    /**
     * 生成所有学生的预警记录
     */
    @Override
    @Transactional
    public void generateWarningsForAllStudents() {
        // 1. 清空预警表
        warningLogMapper.delete(null); //传null表示删除所有记录 mybatis-plus

        // 2. 获取所有学生的请假数据
        LocalDateTime fromTime = LocalDateTime.now().minusMonths(1); // 统计最近1个月
        List<LeaveRequest> recentLeaves = leaveRequestMapper.selectByCreateTimeAfter(fromTime);

        // 3. 按学生分组统计请假次数
        Map<Integer, Long> leaveCountByStudent = recentLeaves.stream()
                .collect(Collectors.groupingBy(LeaveRequest::getUserId, Collectors.counting()));

        // 4. 生成预警列表
        List<WarningLog> warnings = new ArrayList<>();
        long threshold = 3; // 请假超过3次触发预警
        for (Map.Entry<Integer, Long> entry : leaveCountByStudent.entrySet()) {
            if (entry.getValue() > threshold) {
                WarningLog warning = new WarningLog();
                warning.setUserId(entry.getKey());
                warning.setWarningType("请假频繁");
                warning.setMessage("过去一个月请假超过 " + threshold + " 次");
                warning.setCreateTime(LocalDateTime.now());
                warning.setHandled((byte)0);
                warnings.add(warning);
            }
        }

        // 5. 批量插入预警表
        if (!warnings.isEmpty()) {
            this.saveBatch(warnings);
        }
    }
}
