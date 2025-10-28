package com.g5.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.stp.StpUtil;
import com.g5.common.Result;
import com.g5.entity.LeaveRequest;
import com.g5.entity.dto.LeaveRequestDTO;
import com.g5.entity.vo.LeaveRequestVo;
import com.g5.service.ILeaveRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 请假申请表 前端控制器
 * </p>
 *
 * @author Tzp
 * @since 2025-06-09
 */

@RestController
@RequestMapping("/leave-request")
public class LeaveRequestController {

    @Autowired
    private ILeaveRequestService leaveRequestService;



    /**
     * 提交请假申请
     * @param form
     * @return
     */
    //@SaCheckRole("1")   // 限制学生角色
    @PostMapping("/apply")
    public Result apply(@RequestBody LeaveRequestDTO form) {
        System.out.println("=== 进入 apply 方法 ===");
        System.out.println("接收到的表单数据: " + form.toString());

        try {
            StpUtil.checkLogin();
            Long userId = StpUtil.getLoginIdAsLong();

            // 提交请假申请并获取生成的请假申请ID
            Long leaveRequestId = leaveRequestService.submitLeave(userId, form);
            System.out.println("生成的请假申请ID: " + leaveRequestId);

            // 返回包含请假申请ID的成功响应
            Map<String, Object> result = new HashMap<>();
            result.put("leaveRequestId", leaveRequestId);
            result.put("message", "请假申请提交成功");

            return Result.success(result);

        } catch (NotLoginException e) {
            return Result.error("请先登录再提交申请");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("提交申请失败: " + e.getMessage());
        }
    }


    /**
     * 获取我的请假申请列表
     * @return
     */
    @SaCheckRole("1")   // 限制学生角色
    @GetMapping("/myApply")
    public Result<List<LeaveRequestVo>> getMyLeaveList() {
        Integer studentId = Integer.valueOf(StpUtil.getLoginId().toString());
        List<LeaveRequestVo> list = leaveRequestService.getMyLeaveList(studentId);
        return Result.success(list);
    }

    /**
     * 学生销假
     * @param leaveRequestId
     * @return
     */
    @SaCheckRole("1")   // 限制学生角色
    @PostMapping("/cancel/{leaveRequestId}")
    public Result cancelLeave(@PathVariable Integer leaveRequestId) {
        Integer studentId = Integer.valueOf(StpUtil.getLoginId().toString());
        System.out.println("studentId: " + studentId + " leaveRequestId: " + leaveRequestId + "");
        boolean success = leaveRequestService.cancelLeave(leaveRequestId, studentId);
        if (success) {
            return Result.success("销假成功");
        } else {
            return Result.error("销假失败，请检查条件是否符合");
        }
    }


    /**
     * 导出请假记录
     * @param response
     * @throws IOException
     */
    @SaCheckRole("2") //限制老师角色
    @GetMapping("/exportByMonth/{months}" )
    public void exportLastMonth(@PathVariable("months") int months,
                                HttpServletResponse response) throws IOException {
        System.out.println("进入导出方法: "+"months = "+months);

        leaveRequestService.exportLastMonthLeaveRequests(months,response);
    }


    /**
     * 学生撤销请假申请（逻辑删除）
     * @param leaveRequestId 请假申请ID
     * @return 操作结果
     */
    @DeleteMapping("/delete/{leaveRequestId}")
    public Result revokeLeaveRequest(@PathVariable Integer leaveRequestId) {
        try {
            // 使用 SaToken 获取当前登录用户
            if (!StpUtil.isLogin()) {
                return Result.error("用户未登录");
            }

            // 获取当前用户ID
            Object userIdObj = StpUtil.getLoginId();
            Integer currentUserId;

            try {
                currentUserId = Integer.valueOf(userIdObj.toString());
            } catch (NumberFormatException e) {
                return Result.error("用户ID格式错误");
            }

            // 调用服务层撤销请假申请
            boolean success = leaveRequestService.revokeLeaveRequest(leaveRequestId, currentUserId);

            if (success) {
                return Result.success("撤销请假申请成功");
            } else {
                return Result.error("撤销请假申请失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("系统异常，请稍后重试");
        }
    }





    /**
     * 查询审批人已审批的请假申请
     * @param approverId 审批人ID
     */
    @GetMapping("/approvedByApproverId/{approverId}")
    public Result<List<LeaveRequestVo>> getApprovedByApproverId(@PathVariable Integer approverId) {
        System.out.println("打印待查询的审批人ID: " + approverId);

        List<LeaveRequestVo> list = leaveRequestService.getApprovedByApproverId(approverId);

        return new Result<>(200, "查询成功", list);
    }


}
