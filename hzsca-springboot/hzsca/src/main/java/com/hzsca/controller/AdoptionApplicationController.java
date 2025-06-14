package com.hzsca.controller;

import com.hzsca.entity.AdoptionApplication;
import com.hzsca.service.AdoptionApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/adoption-assessments")
@CrossOrigin
public class AdoptionApplicationController {
    @Autowired
    private AdoptionApplicationService adoptionApplicationService;

    // 提交领养评估
    @PostMapping("")
    public Map<String, Object> submitAssessment(@RequestBody AdoptionApplication application) {
        Map<String, Object> result = new HashMap<>();

        int rows = adoptionApplicationService.submitAssessment(application);
        if (rows > 0) {
            result.put("success", true);
            result.put("message", "评估提交成功");
        } else {
            result.put("success", false);
            result.put("message", "评估提交失败");
        }
        return result;
    }

    // 获取单个评估信息
    @GetMapping("/{assessmentId}")
    public AdoptionApplication getAssessment(@PathVariable Integer assessmentId) {
        return adoptionApplicationService.getAssessmentById(assessmentId);
    }

    // 获取领养人的所有评估
    @GetMapping("/adopter/{adopterId}")
    public List<AdoptionApplication> getAdopterAssessments(@PathVariable Integer adopterId) {
        return adoptionApplicationService.getAssessmentsByAdopterId(adopterId);
    }

    // 获取所有评估（分页）
    @GetMapping("")
    public Map<String, Object> getAllAssessments(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String name) {
        Map<String, Object> result = new HashMap<>();
        try {
            Map<String, Object> data = adoptionApplicationService.getAllAssessments(page, size, name);
            result.put("code", "200");
            result.put("data", data);
        } catch (Exception e) {
            result.put("code", "500");
            result.put("message", e.getMessage());
        }
        return result;
    }

    // 更新评估信息
    @PutMapping("/{assessmentId}")
    public Map<String, Object> updateAssessment(
            @PathVariable Integer assessmentId,
            @RequestBody AdoptionApplication application) {
        Map<String, Object> result = new HashMap<>();

        application.setAssessmentId(assessmentId);
        int rows = adoptionApplicationService.updateAssessment(application);

        if (rows > 0) {
            result.put("success", true);
            result.put("message", "评估更新成功");
        } else {
            result.put("success", false);
            result.put("message", "评估更新失败");
        }
        return result;
    }

    // 通过申请
    @PutMapping("/{assessmentId}/approve")
    public Map<String, Object> approveAssessment(@PathVariable Integer assessmentId) {
        Map<String, Object> result = new HashMap<>();
        try {
            adoptionApplicationService.approveAssessment(assessmentId);
            result.put("code", "200");
            result.put("message", "申请已通过");
        } catch (Exception e) {
            result.put("code", "500");
            result.put("message", "处理失败：" + e.getMessage());
        }
        return result;
    }

    // 拒绝申请
    @PutMapping("/{assessmentId}/reject")
    public Map<String, Object> rejectAssessment(@PathVariable Integer assessmentId) {
        Map<String, Object> result = new HashMap<>();
        try {
            adoptionApplicationService.rejectAssessment(assessmentId);
            result.put("code", "200");
            result.put("message", "申请已拒绝");
        } catch (Exception e) {
            result.put("code", "500");
            result.put("message", "处理失败：" + e.getMessage());
        }
        return result;
    }
}