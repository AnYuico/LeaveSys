package com.g5.entity.vo;

import lombok.Data;

@Data
public class AttendanceStatVo {
    private Long totalRequests;
    private Long applyingRequests;
    private Long approvedRequests;
    private Long rejectedRequests;
    private Long cancelRequests;
    private String mostLeaveTypeName;
}
