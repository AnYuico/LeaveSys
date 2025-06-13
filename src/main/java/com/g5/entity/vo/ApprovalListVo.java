package com.g5.entity.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ApprovalListVo {

    private Integer approvalFlowId;     // 审批流程ID
    private Integer leaveRequestId;     // 请假申请ID

    private String studentName;         // 学生姓名
    private String leaveType;           // 请假类型
    private String reason;              // 请假事由

    private LocalDateTime startTime;    // 请假开始时间
    private LocalDateTime endTime;      // 请假结束时间
    private LocalDateTime submitTime;   // 学生提交时间

    private String proofFileUrl;        // 上传的证明材料（文件路径或链接）
    private String status;              // 审批状态（如“待审批”、“已通过”、“已驳回”）
}