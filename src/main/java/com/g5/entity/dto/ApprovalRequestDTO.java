package com.g5.entity.dto;

import lombok.Data;

@Data
public class ApprovalRequestDTO {
    private Integer leaveRequestId;
    private Byte result;
    private String comment;
}
