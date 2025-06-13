package com.g5.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 请假状态变更记录表
 * </p>
 *
 * @author Tzp
 * @since 2025-06-09
 */
@Getter
@Setter
@TableName("leave_status_log")
public class LeaveStatusLog implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 状态变更记录ID
     */
    @TableId(value = "leave_status_log_id", type = IdType.AUTO)
    private Integer leaveStatusLogId;

    /**
     * 请假申请ID，外键关联请假申请表
     */
    @TableField("leave_request_id")
    private Integer leaveRequestId;

    /**
     * 变更前状态
     */
    @TableField("previous_status")
    private Byte previousStatus;

    /**
     * 变更后状态
     */
    @TableField("new_status")
    private Byte newStatus;

    /**
     * 操作人ID，外键关联用户表
     */
    @TableField("changed_by")
    private Integer changedBy;

    /**
     * 状态变更时间
     */
    @TableField("change_time")
    private LocalDateTime changeTime;

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
