package com.hzsca.controller;

import com.hzsca.entity.AdoptionApplication;
import com.hzsca.service.AdoptionApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class AdoptionApplicationController {
    @Autowired
    private AdoptionApplicationService adoptionApplicationService;

    // 提交领养评估
    @PostMapping("/adoption-assessments")
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
    @GetMapping("/adoption-assessments/{assessmentId}")
    public AdoptionApplication getAssessment(@PathVariable Integer assessmentId) {
        return adoptionApplicationService.getAssessmentById(assessmentId);
    }

    // 获取领养人的所有评估
    @GetMapping("/adoption-assessments/adopter/{adopterId}")
    public List<AdoptionApplication> getAdopterAssessments(@PathVariable Integer adopterId) {
        return adoptionApplicationService.getAssessmentsByAdopterId(adopterId);
    }

    // 更新评估信息
    @PutMapping("/adoption-assessments/{assessmentId}")
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
}