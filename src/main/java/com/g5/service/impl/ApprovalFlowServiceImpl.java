package com.g5.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.g5.common.LeaveTypeEnum;
import com.g5.entity.LeaveMaterial;
import com.g5.entity.User;
import com.g5.entity.dto.ApprovalRequestDTO;
import com.g5.entity.ApprovalFlow;
import com.g5.entity.LeaveRequest;
import com.g5.entity.vo.ApprovalListVo;
import com.g5.mapper.ApprovalFlowMapper;
import com.g5.mapper.LeaveMaterialMapper;
import com.g5.mapper.LeaveRequestMapper;
import com.g5.mapper.UserMapper;
import com.g5.service.IApprovalFlowService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 审批流程表 服务实现类
 * </p>
 *
 * @author Tzp
 * @since 2025-06-09
 */
@Service
public class ApprovalFlowServiceImpl extends ServiceImpl<ApprovalFlowMapper, ApprovalFlow> implements IApprovalFlowService {


    @Autowired
    private ApprovalFlowMapper approvalFlowMapper;

    @Autowired
    private LeaveRequestMapper leaveRequestMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private LeaveMaterialMapper leaveMaterialMapper;

    /**
     * 审批
     * @param dto
     * @param approverId
     * @param sequence
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean approve(ApprovalRequestDTO dto, Integer approverId, byte sequence) {
        //1.查询该请假申请当前的审批记录(状态应为待审批 1 ，审批顺序匹配sequence)
        QueryWrapper<ApprovalFlow> query = new QueryWrapper<>();
        query.eq("leave_request_id", dto.getLeaveRequestId())
                .eq("sequence", sequence)
                .eq("result", (byte)1)  // 1-待审批
                .eq("is_deleted", 0);
        ApprovalFlow approvalFlow = approvalFlowMapper.selectOne(query);
        if (approvalFlow == null){
            return false; //没有找到审批记录，可能已经审批或顺序错了
        }

        // 先根据请假申请ID查询请假申请实体
        LeaveRequest leaveRequest = leaveRequestMapper.selectById(dto.getLeaveRequestId());
        if (leaveRequest == null) {
            throw new RuntimeException("请假申请不存在");
        }

        //2.更新审批流程记录
        System.out.println("打印一下Dto.result: "+dto.getResult());
        approvalFlow.setResult(dto.getResult());
        approvalFlow.setComment(dto.getComment());
        approvalFlow.setApprovalTime(LocalDateTime.now());
        approvalFlow.setUpdateTime(LocalDateTime.now());
        approvalFlow.setApproverId(dto.getApproverId());
        approvalFlowMapper.updateById(approvalFlow);

        //3. 根据审批结果更新请假申请状态
        if (dto.getResult() == 2) {
            // 审批通过
            // 查看是否还有下一级审批流程
            QueryWrapper<ApprovalFlow> nextQuery = new QueryWrapper<>();
            nextQuery.eq("leave_request_id", dto.getLeaveRequestId())
                    .eq("sequence", sequence + 1)
                    .eq("is_deleted", 0);

            ApprovalFlow nextFlow = approvalFlowMapper.selectOne(nextQuery);
            if (nextFlow == null) {
                // 所有审批完成，请假申请状态设为"已批准"
                leaveRequest.setStatus((byte) 2); // 2-已批准
            } else {
                // 还有下一级审批，状态保持为"申请中"（即1）
                // 这里可以不更新状态，因为本来就是1
                // 但如果之前状态不是1，我们可以将其设置为1
                if (leaveRequest.getStatus() != 1) {
                    leaveRequest.setStatus((byte) 1); // 1-申请中
                }
            }
        } else if (dto.getResult() == 3) {
            // 审批拒绝 - 修复：立即更新请假申请状态为"已拒绝"
            leaveRequest.setStatus((byte) 3); // 3-已拒绝

            // 可选：拒绝后，可以取消后续的所有待审批流程
            // cancelPendingApprovals(dto.getLeaveRequestId(), sequence);
        }

        // 更新请假申请
        leaveRequest.setUpdateTime(LocalDateTime.now());
        leaveRequestMapper.updateById(leaveRequest);

        return true;
    }


    /**
     * 获取待审批列表
     *
     * @param approverId
     * @param sequence
     * @return
     */
    @Override
    public List<ApprovalListVo> getPendingList(Integer approverId, byte sequence) {
        // 第一步：查出所有待审批的流程记录
        LambdaQueryWrapper<ApprovalFlow> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ApprovalFlow::getSequence, sequence)
                .eq(ApprovalFlow::getResult, (byte)1);  // 1 = 待审批

        List<ApprovalFlow> flows = approvalFlowMapper.selectList(wrapper);

        // 第二步：将每条流程记录转换为 ApprovalRequestVo
        List<ApprovalListVo> voList = new ArrayList<>();
        for (ApprovalFlow flow : flows) {
            LeaveRequest leaveRequest = leaveRequestMapper.selectById(flow.getLeaveRequestId());

            // 修复：添加空值检查
            if (leaveRequest == null) {
                continue; // 跳过无效的请假申请
            }

            LeaveMaterial leaveMaterial = leaveMaterialMapper.selectByLeaveRequestId(leaveRequest.getLeaveRequestId());
            User student = userMapper.selectById(leaveRequest.getUserId());
            System.out.println("学生信息：");
            System.out.println(student.toString());
            // 修复：添加学生空值检查
            if (student == null) {
                continue; // 跳过无效的学生记录
            }

            ApprovalListVo vo = new ApprovalListVo();
            vo.setApprovalFlowId(flow.getApprovalFlowId());
            vo.setLeaveRequestId(flow.getLeaveRequestId());

            vo.setStudentName(student.getRealName());
            vo.setLeaveType(LeaveTypeEnum.getDescByCode(leaveRequest.getLeaveType()));
            vo.setReason(leaveRequest.getReason());
            vo.setStartTime(leaveRequest.getStartDate().atStartOfDay());
            vo.setEndTime(leaveRequest.getEndDate().atStartOfDay());
            vo.setSubmitTime(leaveRequest.getCreateTime());

            // 修复：安全处理请假材料
            if (leaveMaterial != null) {
                vo.setProofFileUrl(leaveMaterial.getFilePath());
            } else {
                vo.setProofFileUrl(null); // 或者设置为空字符串 ""
            }

            vo.setStatus("待审批");

            voList.add(vo);
        }

        return voList;
    }

}
