package com.hzsca.controller;

import com.hzsca.common.Result;
import com.hzsca.entity.Activity;
import com.hzsca.mapper.ActivityMapper;
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
            activityMapper.insertActivity(activity);
            return Result.success();
        } catch (Exception e) {
            return Result.error("500", "添加活动失败");
        }
    }

    @PutMapping("/update")
    public Result updateActivity(@RequestBody Activity activity) {
        try {
            activityMapper.updateActivity(activity);
            return Result.success();
        } catch (Exception e) {
            return Result.error("500", "更新活动失败");
        }
    }

    @DeleteMapping("/delete/{id}")
    public Result deleteActivity(@PathVariable Integer id) {
        try {
            activityMapper.deleteActivity(id);
            return Result.success();
        } catch (Exception e) {
            return Result.error("500", "删除活动失败");
        }
    }

    @PostMapping("/upload")
    public Result uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            String fileName = UUID.randomUUID().toString() +
                    file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
            String uploadPath = "uploads/activity/";
            File dir = new File(uploadPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File dest = new File(uploadPath + fileName);
            file.transferTo(dest);
            return Result.success("activity/" + fileName);
        } catch (IOException e) {
            return Result.error("500", "图片上传失败");
        }
    }
}