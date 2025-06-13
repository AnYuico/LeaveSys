package com.g5.service.task;

import com.g5.service.IWarningLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class WarningTask {

    @Autowired
    private IWarningLogService warningLogService;

    /**
     * 每天凌晨1点执行一次，扫描生成预警
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void scanAndGenerateWarnings() {
        warningLogService.generateWarningsForAllStudents();
    }
}

