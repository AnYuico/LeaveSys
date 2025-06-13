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
 * 请假异常预警记录表
 * </p>
 *
 * @author Tzp
 * @since 2025-06-09
 */
@Getter
@Setter
@TableName("warning_log")
public class WarningLog implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 预警记录ID
     */
    @TableId(value = "warning_log_id", type = IdType.AUTO)
    private Integer warningLogId;

    /**
     * 用户ID，外键关联user表
     */
    @TableField("user_id")
    private Integer userId;

    /**
     * 请假申请ID，外键关联请假申请表
     */
    @TableField("leave_request_id")
    private Integer leaveRequestId;

    /**
     * 预警类型，例如: 未审批超时、异常请假次数等
     */
    @TableField("warning_type")
    private String warningType;

    /**
     * 预警内容或描述
     */
    @TableField("message")
    private String message;

    /**
     * 是否处理:0-未处理,1-已处理
     */
    @TableField("handled")
    private Byte handled;

    /**
     * 处理人ID，外键关联用户表
     */
    @TableField("handled_by")
    private Integer handledBy;

    /**
     * 处理时间
     */
    @TableField("handled_time")
    private LocalDateTime handledTime;

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
