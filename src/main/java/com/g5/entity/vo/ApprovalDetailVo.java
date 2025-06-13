package com.g5.entity.vo;


import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 请假审批详情视图对象
 */
@Data
public class ApprovalDetailVo {

    // ===== 基础请假申请信息 =====
    private Integer leaveRequestId;

    private String studentName;

    private String studentNumber;

    private String leaveTypeName; // 病假 / 事假等

    private String reason;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private String status; // 当前状态：审批中 / 已通过 / 被驳回

    private String proofUrl; // 上传的证明材料链接

    private LocalDateTime submitTime;


}

