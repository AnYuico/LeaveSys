package com.g5.entity.vo;

import lombok.Data;



import lombok.Data;

/**
 * @description 教师端请假统计页面的视图对象（VO）
 * 用于封装当前月份的请假数据统计结果，供前端展示饼图和统计信息使用。
 */
@Data
public class AttendanceStatVo {

    /**
     * 本月总请假申请数量
     */
    private Long totalRequests;

    /**
     * 当前处于“申请中”状态的请假申请数量
     */
    private Long applyingRequests;

    /**
     * 已批准的请假申请数量
     */
    private Long approvedRequests;

    /**
     * 已拒绝的请假申请数量
     */
    private Long rejectedRequests;

    /**
     * 已销假的请假申请数量
     */
    private Long cancelRequests;

    /**
     * 本月最常见的请假类型（如“病假”、“事假”等）
     */
    private String mostLeaveTypeName;
}

