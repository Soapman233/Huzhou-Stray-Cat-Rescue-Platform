package com.hzsca.controller;

import com.hzsca.common.Result;
import com.hzsca.entity.Activity;
import com.hzsca.mapper.ActivityMapper;
import com.hzsca.service.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Tag(name = "活动管理", description = "活动的增删改查接口")
@RestController
@RequestMapping("/api/activity")
@CrossOrigin(origins = "*")
public class ActivityController {

    @Autowired
    private ActivityMapper activityMapper;

    @Autowired
    private FileService fileService;

    @Operation(summary = "获取所有活动", description = "获取系统中所有活动的列表")
    @GetMapping("/list")
    public Result getAllActivities() {
        try {
            List<Activity> activities = activityMapper.findAllActivities();
            return Result.success(activities);
        } catch (Exception e) {
            return Result.error("500", "获取活动列表失败");
        }
    }

    @Operation(summary = "添加活动", description = "添加一个新的活动")
    @PostMapping("/add")
    public Result addActivity(@RequestBody Activity activity) {
        try {
            if (activity.getTitle() == null || activity.getDate() == null ||
                    activity.getLocation() == null || activity.getDetail() == null ||
                    activity.getImgurl() == null) {
                return Result.error("400", "缺少必要字段");
            }
            activityMapper.insertActivity(activity);
            return Result.success();
        } catch (Exception e) {
            e.printStackTrace(); // 添加错误日志
            return Result.error("500", "添加活动失败: " + e.getMessage());
        }
    }

    @Operation(summary = "更新活动", description = "根据活动ID更新活动信息")
    @PutMapping("/update")
    public Result updateActivity(@RequestBody Activity activity) {
        try {
            if (activity.getId() == null) {
                return Result.error("400", "缺少活动ID");
            }
            activityMapper.updateActivity(activity);
            return Result.success();
        } catch (Exception e) {
            e.printStackTrace(); // 添加错误日志
            return Result.error("500", "更新活动失败: " + e.getMessage());
        }
    }

    @Operation(summary = "删除活动", description = "根据活动ID删除活动")
    @DeleteMapping("/delete/{id}")
    public Result deleteActivity(@Parameter(description = "活动ID") @PathVariable Integer id) {
        try {
            activityMapper.deleteActivity(id);
            return Result.success();
        } catch (Exception e) {
            e.printStackTrace(); // 添加错误日志
            return Result.error("500", "删除活动失败: " + e.getMessage());
        }
    }

    @Operation(summary = "上传活动图片", description = "上传活动相关的图片文件")
    @PostMapping("/upload")
    public Result uploadImage(@Parameter(description = "图片文件") @RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return Result.error("400", "文件为空");
            }
            String imageUrl = fileService.uploadFile(file);
            return Result.success(imageUrl);
        } catch (Exception e) {
            e.printStackTrace(); // 添加错误日志
            return Result.error("500", "图片上传失败: " + e.getMessage());
        }
    }
}