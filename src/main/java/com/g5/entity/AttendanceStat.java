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
 * 请假统计数据表
 * </p>
 *
 * @author Tzp
 * @since 2025-06-09
 */
@Getter
@Setter
@TableName("attendance_stat")
public class AttendanceStat implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 统计记录ID
     */
    @TableId(value = "attendance_stat_id", type = IdType.AUTO)
    private Integer attendanceStatId;

    /**
     * 用户ID，外键关联user表
     */
    @TableField("user_id")
    private Integer userId;

    /**
     * 统计年份，例如2025
     */
    @TableField("stat_year")
    private Integer statYear;

    /**
     * 统计月份，例如5
     */
    @TableField("stat_month")
    private Byte statMonth;

    /**
     * 请假总天数
     */
    @TableField("total_leave_days")
    private Integer totalLeaveDays;

    /**
     * 请假次数
     */
    @TableField("leave_count")
    private Integer leaveCount;

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
