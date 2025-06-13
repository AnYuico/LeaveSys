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
 * 请假附件材料表
 * </p>
 *
 * @author Tzp
 * @since 2025-06-09
 */
@Getter
@Setter
@TableName("leave_material")
public class LeaveMaterial implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 请假材料ID
     */
    @TableId(value = "leave_material_id", type = IdType.AUTO)
    private Integer leaveMaterialId;

    /**
     * 请假申请ID，外键关联请假申请表
     */
    @TableField("leave_request_id")
    private Integer leaveRequestId;

    /**
     * 材料类型，例如医院证明、家长意见书等
     */
    @TableField("material_type")
    private String materialType;

    /**
     * 附件文件存储路径
     */
    @TableField("file_path")
    private String filePath;

    /**
     * 附件描述或说明
     */
    @TableField("description")
    private String description;

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
