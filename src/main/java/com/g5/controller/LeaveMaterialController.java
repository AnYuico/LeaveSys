package com.g5.controller;

import com.g5.common.Result;
import com.g5.entity.LeaveMaterial;
import com.g5.service.ILeaveMaterialService;
import com.g5.util.MinioUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 请假附件材料表 前端控制器
 * </p>
 *
 * @author Tzp
 * @since 2025-06-09
 */
@RestController
@RequestMapping("/leave-material")
public class LeaveMaterialController {


    @Autowired
    private MinioUtil minioUtil;

    @Autowired
    private ILeaveMaterialService leaveMaterialService;

    /**
     * 上传请假附件图片
     * @param file
     * @param leaveRequestId
     * @param materialType
     * @param description
     * @return
     */
    @PostMapping("/upload")
    public Result uploadImage(@RequestParam("file") MultipartFile file,
                              @RequestParam("leaveRequestId") Integer leaveRequestId,
                              @RequestParam("materialType") String materialType,
                              @RequestParam(value = "description", required = false) String description) {
        if (file.isEmpty()) {
            return Result.error("文件不能为空");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return Result.error("只允许上传图片文件");
        }

        try {
            String fileUrl = minioUtil.uploadFile(file);

            LeaveMaterial material = new LeaveMaterial();
            material.setLeaveRequestId(leaveRequestId);
            material.setMaterialType(materialType);
            material.setFilePath(fileUrl);
            material.setDescription(description);
            material.setCreateTime(LocalDateTime.now());
            material.setUpdateTime(LocalDateTime.now());
            material.setIsDeleted((byte) 0);

            leaveMaterialService.save(material);

            return Result.success(fileUrl);
        } catch (Exception e) {
            return Result.error("上传失败：" + e.getMessage());
        }
    }

    /**
     * 根据leaveRequestId获取请假材料图片链接
     * @param leaveRequestId 请假请求ID
     * @return 包含图片链接的结果
     */
    @GetMapping("/getPicByLeaveRequestId")
    public Result getByLeaveRequestId(@RequestParam("leaveRequestId") Integer leaveRequestId) {
        try {
            List<LeaveMaterial> materials = leaveMaterialService.lambdaQuery()
                    .eq(LeaveMaterial::getLeaveRequestId, leaveRequestId)
                    .eq(LeaveMaterial::getIsDeleted, 0)
                    .list();

            if (materials.isEmpty()) {
                return Result.error("未找到相关材料");
            }

            List<String> urls = materials.stream()
                    .map(LeaveMaterial::getFilePath)
                    .collect(Collectors.toList());

            return Result.success(urls);
        } catch (Exception e) {
            return Result.error("查询失败：" + e.getMessage());
        }
    }




}
