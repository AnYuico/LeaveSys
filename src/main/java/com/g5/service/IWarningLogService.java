package com.g5.service;

import com.g5.entity.WarningLog;
import com.baomidou.mybatisplus.extension.service.IService;
import com.g5.entity.vo.WarningLogVo;

import java.util.List;

/**
 * <p>
 * 请假异常预警记录表 服务类
 * </p>
 *
 * @author Tzp
 * @since 2025-06-09
 */
public interface IWarningLogService extends IService<WarningLog> {

    /**
     * 获取当前预警记录
     * @return List<WarningLogVo>
     */
    List<WarningLogVo> getCurrentWarnings();

    /**
     * 生成所有学生的预警记录
     */
    void generateWarningsForAllStudents();
}
