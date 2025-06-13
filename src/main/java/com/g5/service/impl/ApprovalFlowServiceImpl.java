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
    public boolean approve(ApprovalRequestDTO dto, Integer approverId, byte sequence) {
        //1.查询该请假申请当前的审批记录(状态应为待审批 1 ，审批顺序匹配sequence)
        QueryWrapper<ApprovalFlow> query = new QueryWrapper<>();
        query.eq("leave_request_id",dto.getLeaveRequestId())
             .eq("sequence",sequence)
             .eq("result",1)
             .eq("is_deleted",0);
        ApprovalFlow approvalFlow = approvalFlowMapper.selectOne(query);
        if (approvalFlow == null){
            return false; //没有找到审批记录，可能已经审批或顺序错了
        }



        //2.更新审批流程记录
        approvalFlow.setResult(dto.getResult());
        approvalFlow.setComment(dto.getComment());
        approvalFlow.setApprovalTime(LocalDateTime.now());
        approvalFlowMapper.updateById(approvalFlow);


        // 先根据请假申请ID查询请假申请实体
        LeaveRequest leaveRequest = leaveRequestMapper.selectById(dto.getLeaveRequestId());
        if (leaveRequest == null) {
            throw new RuntimeException("请假申请不存在");
        }

        // 3. 若审批通过，触发下一审批流程或更新请假申请状态
        if (dto.getResult() == 2) {
            // 查看是否还有下一级审批流程
            QueryWrapper<ApprovalFlow> nextQuery= new QueryWrapper<>();
            nextQuery.eq("leave_request_id",dto.getLeaveRequestId())
                    .eq("sequence",sequence+1)
                    .eq("is_deleted",0);

            ApprovalFlow nextFlow = approvalFlowMapper.selectOne(nextQuery);
            if (nextFlow == null) {
                // 所有审批完成，请假申请状态可设为“已通过”
                leaveRequest.setStatus((byte) 2);
                leaveRequestMapper.updateById(leaveRequest);
            } else {
                // 等待下一级审批，不做其他操作
            }

        }

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
            LeaveMaterial leaveMaterial = leaveMaterialMapper.selectByLeaveRequestId(leaveRequest.getLeaveRequestId());
            User student = userMapper.selectById(leaveRequest.getUserId());

            ApprovalListVo vo = new ApprovalListVo();
            vo.setApprovalFlowId(flow.getApprovalFlowId());
            vo.setLeaveRequestId(flow.getLeaveRequestId());

            vo.setStudentName(student.getRealName());
            vo.setLeaveType(LeaveTypeEnum.getDescByCode(leaveRequest.getLeaveType()));//  请假类型:1-病假,2-事假,3-婚假,4-产假,5-其他
            vo.setReason(leaveRequest.getReason());
            vo.setStartTime(leaveRequest.getStartDate().atStartOfDay());
            vo.setEndTime(leaveRequest.getEndDate().atStartOfDay());
            vo.setSubmitTime(leaveRequest.getCreateTime());
            //获取请假材料
            vo.setProofFileUrl(leaveMaterial.getFilePath()); // 可选字段
            vo.setStatus("待审批");

            voList.add(vo);
        }

        return voList;
    }

}
