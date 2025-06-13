package com.g5.controller;

import com.g5.common.Result;
import com.g5.entity.vo.WarningLogVo;
import com.g5.service.IWarningLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 请假异常预警记录表 前端控制器
 * </p>
 *
 * @author Tzp
 * @since 2025-06-09
 */
@RestController
@RequestMapping("/warning-log")
public class WarningLogController {

    @Autowired
    private IWarningLogService warningLogService;

    /**
     * 获取当前预警记录
     * @return
     */
    @GetMapping("/list")
    public Result<List<WarningLogVo>> getAllWarnings() {
        List<WarningLogVo> list = warningLogService.getCurrentWarnings();
        return Result.success(list);
    }


}
