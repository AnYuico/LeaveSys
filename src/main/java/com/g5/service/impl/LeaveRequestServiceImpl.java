package com.g5.service.impl;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
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
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
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
     *
     * @param userId
     * @param form
     * @return 新创建的请假申请ID
     */
    @Override
    public Long submitLeave(Long userId, LeaveRequestDTO form) {
        LeaveRequest leaveRequest = new LeaveRequest();
        leaveRequest.setUserId(Math.toIntExact(userId));
        leaveRequest.setStartDate(form.getStartDate());
        leaveRequest.setEndDate(form.getEndDate());
        leaveRequest.setLeaveType(form.getLeaveType());
        leaveRequest.setReason(form.getReason());
        leaveRequest.setStatus((byte) 1); // 默认状态为待审批
        baseMapper.insert(leaveRequest);

        // 获取新创建的请假申请ID
        Long leaveRequestId = Long.valueOf(leaveRequest.getLeaveRequestId());
        System.out.println("新创建的请假申请ID: " + leaveRequestId);

        long days = ChronoUnit.DAYS.between(form.getStartDate(), form.getEndDate()) + 1;

        // 1.请假天数7天以内，老师审批
        ApprovalFlow teacherFlow = new ApprovalFlow();
        teacherFlow.setLeaveRequestId(Math.toIntExact(leaveRequestId)); // 使用获取到的ID
        teacherFlow.setApproverId(2);
        teacherFlow.setSequence((byte) 1);
        teacherFlow.setResult((byte) 1);  // 1待审批
        teacherFlow.setCreateTime(LocalDateTime.now());
        teacherFlow.setIsDeleted((byte) 0);
        approvalFlowMapper.insert(teacherFlow);

        // 2.请假天数>7天，领导审批
        if (days > 7) {
            ApprovalFlow leaderFlow = new ApprovalFlow();
            leaderFlow.setLeaveRequestId(Math.toIntExact(leaveRequestId)); // 使用获取到的ID
            leaderFlow.setApproverId(4);
            leaderFlow.setSequence((byte) 2);
            leaderFlow.setResult((byte) 1);  // 1待审批
            leaderFlow.setCreateTime(LocalDateTime.now());
            leaderFlow.setIsDeleted((byte) 0);
            approvalFlowMapper.insert(leaderFlow);
        }

        // 返回新创建的请假申请ID，而不是userId
        return leaveRequestId;
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
    /**
     * 导出最近 X 个月的请假记录
     * @param months 最近几个月
     * @param response HttpServletResponse
     */
    @Override
    public void exportLastMonthLeaveRequests(int months, HttpServletResponse response) throws IOException {
        System.out.println("进入导出方法 S 层");

        // 1 参数安全校验
        if (months <= 0) {
            months = 1;
        }

        // 2 计算时间范围
        LocalDateTime fromTime = LocalDateTime.now().minusMonths(months);

        // 3 查询数据
        List<LeaveRequest> leaveRequests = selectByCreateTimeAfter(fromTime);

        // 4 实体 → VO 转换
        List<LeaveRequestExportVo> exportList = leaveRequests.stream().map(lr -> {
            LeaveRequestExportVo vo = new LeaveRequestExportVo();
            vo.setLeaveRequestId(lr.getLeaveRequestId());
            vo.setUserId(lr.getUserId());
            vo.setStartDate(lr.getStartDate());
            vo.setEndDate(lr.getEndDate());

            // 请假类型名称
            vo.setLeaveTypeName(LeaveTypeEnum.getDescByCode(lr.getLeaveType()));
            // 状态名称
            vo.setStatusName(StatusEnum.getDescByCode(lr.getStatus()));
            vo.setReason(lr.getReason());

            // ✅ 修复时间类型（直接传 LocalDateTime）
            vo.setCreateTime(lr.getCreateTime());

            return vo;
        }).collect(Collectors.toList());

        // 5设置响应头，确保 Excel 下载正确显示中文
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("过去" + months + "个月的请假信息.xlsx", "UTF-8")
                .replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName);

        //6 写出 Excel 文件
        try (ServletOutputStream outputStream = response.getOutputStream()) {
            EasyExcel.write(outputStream, LeaveRequestExportVo.class)
                    .autoCloseStream(false) // ✅ 避免 EasyExcel 关闭流导致后续异常
                    .sheet("请假信息")
                    .doWrite(exportList);

            outputStream.flush();
        } catch (Exception e) {
            // 7 捕获异常并返回 JSON 提示
            response.reset();
            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");
            response.getWriter().write("{\"code\":500,\"msg\":\"导出失败，请稍后重试\"}");
            e.printStackTrace();
        }
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean revokeLeaveRequest(Integer leaveRequestId, Integer currentUserId) {
        // 1. 查询请假申请是否存在且未被删除
        QueryWrapper<LeaveRequest> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("leave_request_id", leaveRequestId)
                .eq("is_deleted", 0); // 只查询未删除的记录

        LeaveRequest leaveRequest = leaveRequestMapper.selectOne(queryWrapper);
        if (leaveRequest == null) {
            throw new RuntimeException("请假申请不存在或已被删除");
        }

        // 2. 验证权限：只能撤销自己的请假申请
        if (!leaveRequest.getUserId().equals(currentUserId)) {
            throw new RuntimeException("无权撤销他人的请假申请");
        }

        // 3. 验证状态：只能撤销特定状态的申请
        Byte status = leaveRequest.getStatus();
        if (!canRevokeStatus(status)) {
            throw new RuntimeException("当前状态的请假申请不可撤销，只能撤销申请中的请假");
        }

        // 4. 执行逻辑删除
        UpdateWrapper<LeaveRequest> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("leave_request_id", leaveRequestId)
                .eq("is_deleted", 0) // 防止重复删除
                .set("is_deleted", 1)
                .set("update_time", LocalDateTime.now());

        int result = leaveRequestMapper.update(null, updateWrapper);

        // 5. 如果撤销成功，处理相关的关联数据
        if (result > 0) {
            // 撤销相关的审批流程（逻辑删除）
            revokeApprovalFlows(leaveRequestId);

            // 撤销相关的证明材料（逻辑删除）
            revokeLeaveMaterials(leaveRequestId);

            System.out.println("用户 {"+currentUserId+"} 撤销请假申请成功，申请ID: {"+leaveRequestId+"}");

            // 记录操作日志
            // operationLogService.logOperation(currentUserId, "撤销请假申请", "请假申请ID: " + leaveRequestId);
        }

        return result > 0;
    }

    /**
     * 查询审查过的请求
     * @param approverId
     * @return
     */
    @Override
    public List<LeaveRequestVo> getApprovedByApproverId(Integer approverId) {
        // 1.查询该审批人已审批（通过或驳回）的审批记录
        List<ApprovalFlow> approvedFlows = approvalFlowMapper.selectList(
                new QueryWrapper<ApprovalFlow>()
                        .eq("approver_id", approverId)
                        .in("result", Arrays.asList(2, 3))  // 2=通过, 3=驳回
                        .eq("is_deleted", 0)
        );

        if (approvedFlows.isEmpty()) {
            return Collections.emptyList();
        }

        // 2. 提取请假申请ID集合
        List<Integer> leaveRequestIds = approvedFlows.stream()
                .map(ApprovalFlow::getLeaveRequestId)
                .distinct()
                .collect(Collectors.toList());

        // 3.查询这些请假申请详情
        List<LeaveRequest> leaveRequests = leaveRequestMapper.selectBatchIds(leaveRequestIds);
        if (leaveRequests.isEmpty()) {
            return Collections.emptyList();
        }

        // 4.获取所有学生的 userId
        Set<Integer> userIds = leaveRequests.stream()
                .map(LeaveRequest::getUserId)
                .collect(Collectors.toSet());

        // 批量查询学生姓名
        List<User> users = userMapper.selectBatchIds(userIds);
        Map<Integer, String> userNameMap = users.stream()
                .collect(Collectors.toMap(User::getUserId, User::getRealName));

        // 5.封装为 VO
        List<LeaveRequestVo> voList = new ArrayList<>();
        for (LeaveRequest lr : leaveRequests) {
            LeaveRequestVo vo = new LeaveRequestVo();
            vo.setLeaveRequestId(lr.getLeaveRequestId());
            vo.setUserId(lr.getUserId());
            vo.setRealName(userNameMap.getOrDefault(lr.getUserId(), "未知学生"));
            vo.setStartDate(lr.getStartDate());
            vo.setEndDate(lr.getEndDate());
            vo.setReason(lr.getReason());
            vo.setCreateTime(lr.getCreateTime());

            // ✅ 使用枚举类获取中文描述
            if (lr.getLeaveType() != null) {
                vo.setLeaveTypeName(LeaveTypeEnum.getDescByCode(lr.getLeaveType()));
            } else {
                vo.setLeaveTypeName("未知类型");
            }

            if (lr.getStatus() != null) {
                vo.setStatusName(StatusEnum.getDescByCode(lr.getStatus()));
            } else {
                vo.setStatusName("未知状态");
            }

            voList.add(vo);
        }

        return voList;
    }





    /**
     * 检查状态是否允许撤销
     */
    private boolean canRevokeStatus(Byte status) {
        // 根据您的状态定义：1-申请中,2-已批准,3-已拒绝,4-已销假
        // 通常只允许撤销"申请中"状态的申请
        return status == 1; // 只允许撤销"申请中"状态的申请
    }

    /**
     * 撤销审批流程记录（逻辑删除）
     */
    private void revokeApprovalFlows(Integer leaveRequestId) {
        try {
            // 如果有审批流程表，执行逻辑删除
            // 假设 ApprovalFlow 实体也有 is_deleted 字段
            UpdateWrapper<ApprovalFlow> wrapper = new UpdateWrapper<>();
            wrapper.eq("leave_request_id", leaveRequestId)
                    .eq("is_deleted", 0)
                    .set("is_deleted", 1)
                    .set("update_time", LocalDateTime.now());

            // approvalFlowMapper.update(null, wrapper);

            System.out.println("撤销请假申请 {"+leaveRequestId+"} 的审批流程记录");
        } catch (Exception e) {
            System.out.println("撤销审批流程记录失败: "+e.getMessage());
            // 不抛出异常，主事务继续执行
        }
    }

    /**
     * 撤销证明材料记录（逻辑删除）
     */
    private void revokeLeaveMaterials(Integer leaveRequestId) {
        try {
            // 如果有证明材料表，执行逻辑删除
            // 假设 LeaveMaterial 实体也有 is_deleted 字段
            UpdateWrapper<LeaveMaterial> wrapper = new UpdateWrapper<>();
            wrapper.eq("leave_request_id", leaveRequestId)
                    .eq("is_deleted", 0)
                    .set("is_deleted", 1)
                    .set("update_time", LocalDateTime.now());

            // leaveMaterialMapper.update(null, wrapper);

            System.out.println("撤销请假申请 {"+leaveRequestId+"} 的证明材料记录");

        } catch (Exception e) {
            System.out.println("撤销证明材料记录失败: "+ e.getMessage());
            // 不抛出异常，主事务继续执行
        }
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
