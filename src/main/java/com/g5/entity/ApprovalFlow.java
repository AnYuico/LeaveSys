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
 * 审批流程表
 * </p>
 *
 * @author Tzp
 * @since 2025-06-09
 */
@Getter
@Setter
@TableName("approval_flow")
public class ApprovalFlow implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 审批流程ID
     */
    @TableId(value = "approval_flow_id", type = IdType.AUTO)
    private Integer approvalFlowId;

    /**
     * 请假申请ID，外键关联请假申请表
     */
    @TableField("leave_request_id")
    private Integer leaveRequestId;

    /**
     * 审批人ID，外键关联用户表
     */
    @TableField("approver_id")
    private Integer approverId;

    /**
     * 审批顺序
     */
    @TableField("sequence")
    private Byte sequence;

    /**
     * 审批结果:1-待审批,2-通过,3-驳回
     */
    @TableField("result")
    private Byte result;

    /**
     * 审批意见
     */
    @TableField("comment")
    private String comment;

    /**
     * 审批时间
     */
    @TableField("approval_time")
    private LocalDateTime approvalTime;

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
