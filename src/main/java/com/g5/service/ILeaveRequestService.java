package com.g5.service;

import com.g5.entity.dto.LeaveRequestDTO;
import com.g5.entity.LeaveRequest;
import com.baomidou.mybatisplus.extension.service.IService;
import com.g5.entity.vo.ApprovalDetailVo;
import com.g5.entity.vo.LeaveRequestVo;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * <p>
 * 请假申请表 服务类
 * </p>
 *
 * @author Tzp
 * @since 2025-06-09
 */
public interface ILeaveRequestService extends IService<LeaveRequest> {

    /**
     * 提交请假申请
     * @param userId
     * @param form
     */
    void submitLeave(Long userId, LeaveRequestDTO form);

    /**
     * 获取审批详情
     * @param leaveRequestId
     * @return
     */
    ApprovalDetailVo getApprovalDetail(Integer leaveRequestId);

    /**
     * 获取我的请假列表
     * @param studentId
     * @return
     */
    List<LeaveRequestVo> getMyLeaveList(Integer studentId);


    /**
     * 销假
     * @param leaveRequestId
     * @param studentId
     * @return
     */
    boolean cancelLeave(Integer leaveRequestId, Integer studentId);

    /**
     * 导出请假记录
     * @param months
     * @param response
     */
    void exportLastMonthLeaveRequests(int months, HttpServletResponse response) throws IOException;
}
