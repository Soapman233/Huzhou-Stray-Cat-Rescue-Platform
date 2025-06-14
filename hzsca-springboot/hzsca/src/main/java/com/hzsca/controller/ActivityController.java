package com.hzsca.controller;

import com.hzsca.common.Result;
import com.hzsca.entity.Activity;
import com.hzsca.mapper.ActivityMapper;
import com.hzsca.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/activity")
@CrossOrigin(origins = "*")
public class ActivityController {

    @Autowired
    private ActivityMapper activityMapper;

    @Autowired
    private FileService fileService;

    @GetMapping("/list")
    public Result getAllActivities() {
        try {
            List<Activity> activities = activityMapper.findAllActivities();
            return Result.success(activities);
        } catch (Exception e) {
            return Result.error("500", "获取活动列表失败");
        }
    }

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

    @DeleteMapping("/delete/{id}")
    public Result deleteActivity(@PathVariable Integer id) {
        try {
            activityMapper.deleteActivity(id);
            return Result.success();
        } catch (Exception e) {
            e.printStackTrace(); // 添加错误日志
            return Result.error("500", "删除活动失败: " + e.getMessage());
        }
    }

    @PostMapping("/upload")
    public Result uploadImage(@RequestParam("file") MultipartFile file) {
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