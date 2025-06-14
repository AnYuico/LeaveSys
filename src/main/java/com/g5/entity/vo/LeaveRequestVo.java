package com.g5.entity.vo;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class LeaveRequestVo {

    private Integer leaveRequestId;

    private LocalDate startDate;

    private LocalDate endDate;

    private String leaveTypeName; // 病假、事假等（根据 leaveType 映射）

    private String reason;

    private String statusName; // 申请中、已批准等（根据 status 映射）

    private LocalDateTime createTime;
}
