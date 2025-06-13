package com.g5.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.stp.StpUtil;
import com.g5.common.Result;
import com.g5.entity.dto.LeaveRequestDTO;
import com.g5.entity.vo.LeaveRequestVo;
import com.g5.service.ILeaveRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

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
    @SaCheckRole("1")   // 限制学生角色
    @PostMapping("/apply")
    public Result apply(@RequestBody LeaveRequestDTO form) {
        try {
            StpUtil.checkLogin();
        } catch (NotLoginException e) {
            return Result.error("请先登录再提交申请");
        }

        Long userId = StpUtil.getLoginIdAsLong();
        leaveRequestService.submitLeave(userId, form);
        return Result.success("请假申请提交成功");
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
    @GetMapping("/exportByMonth/{months}")
    public void exportLastMonth(@PathVariable("months") int months,
                                HttpServletResponse response) throws IOException {
        leaveRequestService.exportLastMonthLeaveRequests(months,response);
    }


}
