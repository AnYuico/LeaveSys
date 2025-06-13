package com.g5.generator;





import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;


import java.util.Collections;

public class CodeGenerator {
    public static void main(String[] args) {
// 修改成你的数据库连接信息
        String url = "jdbc:mysql://anyui.fun:3305/leavesystem?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai";
        String username = "root";
        String password = "tzp1911";

        FastAutoGenerator.create(url, username, password)
                // 全局配置
                .globalConfig(builder -> builder
                        .author("Tzp")  // 设置作者
                        .outputDir(System.getProperty("user.dir") + "/src/main/java") // 代码输出目录
                        .commentDate("yyyy-MM-dd")  // 注释日期格式
                        .disableOpenDir()    // 生成后不打开目录

                )
                // 包配置
                .packageConfig(builder -> builder
                            .parent("com.g5")  // 设置父包名
                            .entity("entity")
                            .mapper("mapper")
                            .service("service")
                            .serviceImpl("service.impl")
                            .controller("controller")
                            .pathInfo(Collections.singletonMap(OutputFile.xml, System.getProperty("user.dir") + "/src/main/resources/mapper")) // mapper.xml 输出目录
                )
                // 策略配置
                .strategyConfig(builder -> builder
                        .addInclude("approval_flow", "attendance_stat", "leave_material", "leave_request", "leave_status_log", "user", "warning_log") // 需要生成的表名，多个表逗号分隔
                        .entityBuilder()
                        .enableLombok()       // Lombok支持
                        .logicDeleteColumnName("deleted") // 逻辑删除字段(如果有)
                        .enableTableFieldAnnotation() // 开启字段注解
                        .build()
                        .controllerBuilder()
                        .enableRestStyle()    // 开启@RestController
                        .enableHyphenStyle()  // url中驼峰转连字符
                        .build()
                )
                .templateEngine(new FreemarkerTemplateEngine()) // 使用Freemarker模板引擎（默认Velocity）
                .execute();  // 执行生成代码
    }

}
