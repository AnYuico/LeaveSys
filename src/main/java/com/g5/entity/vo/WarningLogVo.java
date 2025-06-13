package com.g5.entity.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class WarningLogVo {

    /**
     * 预警ID
     */
    private Integer warningId;

    /**
     * 学生ID
     */
    private Integer studentId;

    /**
     * 学生姓名（可选，方便展示）
     */
    private String studentName;

    /**
     * 学生学号（可选）
     */
    private String studentNumber;

    /**
     * 预警类型（如：请假频繁、请假时间过长等）
     */
    private String warningType;

    /**
     * 预警描述（详细说明触发预警的原因）
     */
    private String description;

    /**
     * 预警时间
     */
    private LocalDateTime warningTime;

    /**
     * 是否已处理（0-未处理，1-已处理）
     */
    private Byte isHandled;
}
