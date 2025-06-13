package com.g5.entity.dto;

import lombok.Data;

import java.time.LocalDate;

/**
 * DTO：用于提交请假申请的数据对象
 */
@Data
public class LeaveRequestDTO {

    // 请假开始日期
    private LocalDate startDate;

    // 请假结束日期
    private LocalDate endDate;

    // 请假类型：1-病假,2-事假,3-婚假,4-产假,5-其他
    private Byte leaveType;

    // 请假事由
    private String reason;

    // 可选：上传证明材料的URL或路径，可视情况添加
    // private String proofUrl;
}

