package com.g5.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import com.g5.common.Result;
import com.g5.entity.vo.AttendanceStatVo;
import com.g5.service.IAttendanceStatService;
import com.g5.service.task.WarningTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 请假统计数据表 前端控制器
 * </p>
 *
 * @author Tzp
 * @since 2025-06-09
 */
@SaCheckRole("2")//限制老师访问
@RestController
@RequestMapping("/attendance-stat")
public class AttendanceStatController {

    @Autowired
    private WarningTask warningTask;

    @Autowired
    private IAttendanceStatService attendanceStatService;

    /**
     * 获取学生请假统计数据
     * @param days
     * @return
     */
    @GetMapping("/summary")
    public Result<AttendanceStatVo> getStudentLeaveStats(@RequestParam(defaultValue = "30") int days) {
        AttendanceStatVo vo = attendanceStatService.getStatsByStudentIdAndDays(days);
        return Result.success(vo);
    }

    /**
     * 手动执行 生成预警
     * @return
     */
    @GetMapping("/run-warning-now")
    public String runNow() {
        warningTask.scanAndGenerateWarnings();
        return "任务已手动执行";
    }

}
