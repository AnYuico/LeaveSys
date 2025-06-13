package com.g5.service.impl;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.g5.common.LeaveTypeEnum;
import com.g5.common.StatusEnum;
import com.g5.entity.LeaveMaterial;
import com.g5.entity.User;
import com.g5.entity.dto.LeaveRequestDTO;
import com.g5.entity.ApprovalFlow;
import com.g5.entity.LeaveRequest;
import com.g5.entity.vo.ApprovalDetailVo;
import com.g5.entity.vo.LeaveRequestExportVo;
import com.g5.entity.vo.LeaveRequestVo;
import com.g5.mapper.ApprovalFlowMapper;
import com.g5.mapper.LeaveMaterialMapper;
import com.g5.mapper.LeaveRequestMapper;
import com.g5.mapper.UserMapper;
import com.g5.service.ILeaveRequestService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 请假申请表 服务实现类
 * </p>
 *
 * @author Tzp
 * @since 2025-06-09
 */
@Service
public class LeaveRequestServiceImpl extends ServiceImpl<LeaveRequestMapper, LeaveRequest> implements ILeaveRequestService {


    @Autowired
    private ApprovalFlowMapper approvalFlowMapper;

    @Autowired
    private LeaveRequestMapper leaveRequestMapper;

    @Autowired
    private LeaveMaterialMapper leaveMaterialMapper;

    @Autowired
    private UserMapper userMapper;

    /**
     * 提交请假表
     * @param userId
     * @param form
     */
    @Override
    public void submitLeave(Long userId, LeaveRequestDTO form) {


        LeaveRequest leaveRequest = new LeaveRequest();
        leaveRequest.setUserId(Math.toIntExact(userId));
        leaveRequest.setStartDate(form.getStartDate());
        leaveRequest.setEndDate(form.getEndDate());
        leaveRequest.setLeaveType(form.getLeaveType());
        leaveRequest.setReason(form.getReason());
        leaveRequest.setStatus((byte) 1); // 默认状态为待审批
        baseMapper.insert(leaveRequest);

        long days = ChronoUnit.DAYS.between(form.getStartDate(), form.getEndDate())+1;
        //1.请假天数7天以内，老师审批
        ApprovalFlow teacherFlow = new ApprovalFlow();
        teacherFlow.setLeaveRequestId(leaveRequest.getLeaveRequestId());
        teacherFlow.setApproverId(2);
        teacherFlow.setSequence((byte)1);
        teacherFlow.setResult((byte)1);  //1待审批
        teacherFlow.setCreateTime(LocalDateTime.now());
        teacherFlow.setIsDeleted((byte)0);
        approvalFlowMapper.insert(teacherFlow);

        //2.请假天数>7天，领导审批
        if (days > 7) {
            ApprovalFlow leaderFlow = new ApprovalFlow();
            leaderFlow.setLeaveRequestId(leaveRequest.getLeaveRequestId());
            leaderFlow.setApproverId(4);
            leaderFlow.setSequence((byte)2);
            leaderFlow.setResult((byte)1);  //1待审批
            leaderFlow.setCreateTime(LocalDateTime.now());
            leaderFlow.setIsDeleted((byte)0);
            approvalFlowMapper.insert(leaderFlow);
        }
    }

    /**
     * 获取请假详情
     * @param leaveRequestId
     * @return
     */
    @Override
    public ApprovalDetailVo getApprovalDetail(Integer leaveRequestId) {
        // 查询请假申请
        LeaveRequest leaveRequest = leaveRequestMapper.selectById(leaveRequestId);
        if (leaveRequest == null) {
            return null;
        }

        //查询请假材料
        LeaveMaterial leaveMaterial = leaveMaterialMapper.selectByLeaveRequestId(leaveRequestId);
        if (leaveMaterial == null) {
            return null;
        }

        // 查询学生信息（假设字段为 userId）
        User student = userMapper.selectById(leaveRequest.getUserId());
        if (student == null) {
            return null;
        }
        // 封装 VO
        ApprovalDetailVo vo = new ApprovalDetailVo();
        vo.setLeaveRequestId(leaveRequest.getLeaveRequestId());
        vo.setStudentName(student.getRealName());
        vo.setStudentNumber("HBUER_"+student.getUserId().toString());
        vo.setLeaveTypeName((LeaveTypeEnum.getDescByCode(leaveRequest.getLeaveType())));
        vo.setReason(leaveRequest.getReason());
        vo.setStartTime(leaveRequest.getStartDate().atStartOfDay());  // LocalDate → LocalDateTime
        vo.setEndTime(leaveRequest.getEndDate().atStartOfDay());

        Byte status = leaveRequest.getStatus();
        String descByCode = StatusEnum.getDescByCode(status);
        vo.setStatus(descByCode);
        vo.setProofUrl(leaveMaterial.getFilePath());
        vo.setSubmitTime(leaveRequest.getCreateTime());

        return vo;
    }

    /**
     * 获取我的请假列表
     * @param studentId
     * @return
     */
    @Override
    public List<LeaveRequestVo> getMyLeaveList(Integer studentId) {
        // 1. 查询数据库中学生的请假申请列表，未删除的
        QueryWrapper<LeaveRequest> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", studentId)
                .eq("is_deleted", 0)
                .orderByDesc("create_time");
        List<LeaveRequest> leaveRequests = leaveRequestMapper.selectList(queryWrapper);

        // 2. 转换成 VO
        List<LeaveRequestVo> voList = leaveRequests.stream().map(leaveRequest -> {
            LeaveRequestVo vo = new LeaveRequestVo();
            vo.setLeaveRequestId(leaveRequest.getLeaveRequestId());
            vo.setStartDate(leaveRequest.getStartDate());
            vo.setEndDate(leaveRequest.getEndDate());
            vo.setReason(leaveRequest.getReason());
            vo.setCreateTime(leaveRequest.getCreateTime());

            // leaveType 转中文
            vo.setLeaveTypeName(LeaveTypeEnum.getDescByCode(leaveRequest.getLeaveType()));

            // status 转中文
            vo.setStatusName(StatusEnum.getDescByCode(leaveRequest.getStatus()));

            return vo;
        }).collect(Collectors.toList());

        return voList;
    }

    /**
     * 销假
     * @param leaveRequestId
     * @param studentId
     * @return
     */
    @Override
    public boolean cancelLeave(Integer leaveRequestId, Integer studentId) {
        // 1. 查询请假记录
        LeaveRequest leaveRequest = leaveRequestMapper.selectById(leaveRequestId);
        if (leaveRequest == null) {
            return false; // 请假记录不存在
        }

        // 2. 判断是否是当前学生提交的申请
        if (!leaveRequest.getUserId().equals(studentId)) {
            return false; // 非本人操作
        }

        // 3. 判断当前状态是否为“已批准” (2)
        if (!leaveRequest.getStatus().equals((byte) 2)) {
            return false; // 不是已批准状态，不能销假
        }


        // 4. 修改状态为“已销假”（4），更新时间
        leaveRequest.setStatus((byte) 4);
        leaveRequest.setUpdateTime(LocalDateTime.now());

        int rows = leaveRequestMapper.updateById(leaveRequest);
        return rows > 0;
    }

    /**
     * 导出请假记录
     * @param months
     * @param response
     */
    @Override
    public void exportLastMonthLeaveRequests(int months, HttpServletResponse response) throws IOException {
        if (months <= 0) {
            months = 1; // 防止非法参数
        }

        LocalDateTime fromTime = LocalDateTime.now().minusMonths(months);

        List<LeaveRequest> leaveRequests = selectByCreateTimeAfter(fromTime);

        // 转换成VO
        List<LeaveRequestExportVo> exportList = leaveRequests.stream().map(lr -> {
            LeaveRequestExportVo vo = new LeaveRequestExportVo();
            vo.setLeaveRequestId(lr.getLeaveRequestId());
            vo.setUserId(lr.getUserId());
            vo.setStartDate(lr.getStartDate());
            vo.setEndDate(lr.getEndDate());
            // 请假类型转换成名称
            vo.setLeaveTypeName(LeaveTypeEnum.getDescByCode(lr.getLeaveType()));
            vo.setReason(lr.getReason());
            // 状态转换
            vo.setStatusName(StatusEnum.getDescByCode(lr.getStatus()));
            vo.setCreateTime(lr.getCreateTime().toLocalDate());
            return vo;
        }).collect(Collectors.toList());

        // 设置响应头
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("过去"+months+"个月的"+"请假信息.xlsx", "UTF-8");
        response.setHeader("Content-disposition", "attachment;filename=" + fileName);

        EasyExcel.write(response.getOutputStream(), LeaveRequestExportVo.class)
                .sheet("请假信息")
                .doWrite(exportList);
    }

    /**
     * 查询指定时间之后的请假记录
     * @param fromTime
     * @return
     */
    public List<LeaveRequest> selectByCreateTimeAfter(LocalDateTime fromTime) {
        QueryWrapper<LeaveRequest> wrapper = new QueryWrapper<>();
        wrapper.ge("create_time", fromTime)
                .eq("is_deleted", 0)
                .orderByDesc("create_time");
        return leaveRequestMapper.selectList(wrapper);
    }
}
