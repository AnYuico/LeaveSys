package com.g5.entity.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.time.LocalDate;



import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;

import com.alibaba.excel.annotation.format.DateTimeFormat;


import java.time.LocalDateTime;


@Data
@ExcelIgnoreUnannotated  // 只导出带有 @ExcelProperty 的字段
public class LeaveRequestExportVo {

    @ExcelProperty("请假申请ID")
    private Integer leaveRequestId;

    @ExcelProperty("学生ID")
    private Integer userId;

    @ExcelProperty("开始日期")
    @DateTimeFormat("yyyy-MM-dd")  // ✅ 确保 Excel 中显示为 yyyy-MM-dd
    private LocalDate startDate;

    @ExcelProperty("结束日期")
    @DateTimeFormat("yyyy-MM-dd")
    private LocalDate endDate;

    @ExcelProperty("请假类型")
    private String leaveTypeName;

    @ExcelProperty("请假事由")
    private String reason;

    @ExcelProperty("状态")
    private String statusName;

    @ExcelProperty("创建时间")
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")  // ✅ 让时间显示完整
    private LocalDateTime createTime;
}
