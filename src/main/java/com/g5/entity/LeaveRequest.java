package com.g5.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 请假申请表
 * </p>
 *
 * @author Tzp
 * @since 2025-06-09
 */
@Getter
@Setter
@TableName("leave_request")
public class LeaveRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 请假申请ID
     */
    @TableId(value = "leave_request_id", type = IdType.AUTO)
    private Integer leaveRequestId;

    /**
     * 申请人ID，外键关联user表
     */
    @TableField("user_id")
    private Integer userId;

    /**
     * 请假开始日期
     */
    @TableField("start_date")
    private LocalDate startDate;

    /**
     * 请假结束日期
     */
    @TableField("end_date")
    private LocalDate endDate;

    /**
     * 请假类型:1-病假,2-事假,3-婚假,4-产假,5-其他
     */
    @TableField("leave_type")
    private Byte leaveType;

    /**
     * 请假事由
     */
    @TableField("reason")
    private String reason;

    /**
     * 请假状态:1-申请中,2-已批准,3-已拒绝,4-已销假
     */
    @TableField("status")
    private Byte status;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField("update_time")
    private LocalDateTime updateTime;

    /**
     * 逻辑删除标志:0-未删除,1-已删除
     */
    @TableField("is_deleted")
    private Byte isDeleted;
}
