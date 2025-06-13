package com.g5.entity.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.time.LocalDate;

@Data
public class LeaveRequestExportVo {
    @ExcelProperty("请假申请ID")
    private Integer leaveRequestId;

    @ExcelProperty("学生ID")
    private Integer userId;

    @ExcelProperty("开始日期")
    private LocalDate startDate;

    @ExcelProperty("结束日期")
    private LocalDate endDate;

    @ExcelProperty("请假类型")
    private String leaveTypeName;

    @ExcelProperty("请假事由")
    private String reason;

    @ExcelProperty("状态")
    private String statusName;

    @ExcelProperty("创建时间")
    private LocalDate createTime;
}
