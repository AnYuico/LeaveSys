package com.g5.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.stp.StpUtil;
import com.g5.common.Result;
import com.g5.entity.dto.ApprovalRequestDTO;
import com.g5.entity.vo.ApprovalDetailVo;
import com.g5.entity.vo.ApprovalListVo;
import com.g5.service.IApprovalFlowService;
import com.g5.service.ILeaveRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 审批流程表 前端控制器
 * </p>
 *
 * @author Tzp
 * @since 2025-06-09
 */

@RestController
@RequestMapping("/approval-flow")
public class ApprovalFlowController {

    @Autowired
    private IApprovalFlowService approvalFlowService;

    @Autowired
    private ILeaveRequestService leaveRequestService;

    /**
     * 获取请假申请详情
     * @param leaveRequestId
     * @return
     */
    @GetMapping("/detail/{leaveRequestId}")
    public Result<ApprovalDetailVo> getApprovalDetail(@PathVariable Integer leaveRequestId) {

        // 调用 service 查询详情
        ApprovalDetailVo detail = leaveRequestService.getApprovalDetail(leaveRequestId);

        if (detail == null) {
            return Result.error("未找到该请假申请");
        }

        return Result.success(detail);
    }


    /**
     * 教师获取待审批列表
     * @return
     */
    @SaCheckRole("2") // 教师
    @GetMapping("teacher-pending")
    public Result<List<ApprovalListVo>> getTeacherPendingList() {
        Integer approverId = Integer.valueOf(StpUtil.getLoginId().toString());
        List<ApprovalListVo> list = approvalFlowService.getPendingList(approverId, (byte)1);
        String msg = "";
        if (list.isEmpty()){
            msg = "暂无待审批申请";
        }else {
            msg = "您有" + list.size() + "条待审批申请";
        }
        return Result.of(200,msg,list);
    }

    /**
     * 领导获取待审批列表
     * @return
     */
    @SaCheckRole("4") // 领导
    @GetMapping("leader-pending")
    public Result<List<ApprovalListVo>> getLeaderPendingList() {
        Integer approverId = Integer.valueOf(StpUtil.getLoginId().toString());
        List<ApprovalListVo> list = approvalFlowService.getPendingList(approverId, (byte)2);
        String msg = "";
        if (list.isEmpty()){
            msg = "暂无待审批申请";
        }else {
            msg = "您有" + list.size() + "条待审批申请";
        }
        return Result.of(200,msg,list);
    }




    /**
     * 老师审批
     * @param dto
     * @return
     */
    @SaCheckRole("2")
    @PostMapping("teacher-approval")
    public Result teacherApproval(@RequestBody ApprovalRequestDTO dto){
        Integer approverId = Integer.valueOf(StpUtil.getLoginId().toString());

        boolean success = approvalFlowService.approve(dto,approverId,(byte)1); //sequence 1等于老师审批
        if (success)return Result.success("审批成功");
        else return Result.error("审批失败");
    }


    /**
     * 领导审批
     * @param dto
     * @return
     */
    @SaCheckRole("4")  // 领导
    @PostMapping("leader-approval")
    public Result leaderApproval(@RequestBody ApprovalRequestDTO dto){
        Integer approverId = Integer.valueOf(StpUtil.getLoginId().toString());

        boolean success = approvalFlowService.approve(dto, approverId, (byte)2); // sequence 2 = 领导审批
        if (success) return Result.success("审批成功");
        else return Result.error("审批失败");
    }


}
