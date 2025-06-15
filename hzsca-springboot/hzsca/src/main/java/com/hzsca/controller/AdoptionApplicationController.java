package com.hzsca.controller;

import com.hzsca.entity.AdoptionApplication;
import com.hzsca.service.AdoptionApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "领养申请管理", description = "处理领养申请的评估、审批等操作")
@RestController
@RequestMapping("/api/adoption-assessments")
@CrossOrigin
public class AdoptionApplicationController {
    @Autowired
    private AdoptionApplicationService adoptionApplicationService;

    @Operation(summary = "提交领养评估", description = "提交新的领养申请评估")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "提交成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误")
    })
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

    @Operation(summary = "获取评估信息", description = "根据评估ID获取单个评估信息")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "获取成功"),
            @ApiResponse(responseCode = "404", description = "评估不存在")
    })
    @GetMapping("/{assessmentId}")
    public AdoptionApplication getAssessment(
            @Parameter(description = "评估ID") @PathVariable Integer assessmentId) {
        return adoptionApplicationService.getAssessmentById(assessmentId);
    }

    @Operation(summary = "获取领养人评估", description = "获取指定领养人的所有评估记录")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "获取成功")
    })
    @GetMapping("/adopter/{adopterId}")
    public List<AdoptionApplication> getAdopterAssessments(
            @Parameter(description = "领养人ID") @PathVariable Integer adopterId) {
        return adoptionApplicationService.getAssessmentsByAdopterId(adopterId);
    }

    @Operation(summary = "获取所有评估", description = "分页获取所有评估记录，支持按名称搜索")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "获取成功"),
            @ApiResponse(responseCode = "500", description = "服务器错误")
    })
    @GetMapping("")
    public Map<String, Object> getAllAssessments(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "搜索名称") @RequestParam(required = false) String name) {
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

    @Operation(summary = "更新评估", description = "更新指定评估的信息")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "更新成功"),
            @ApiResponse(responseCode = "404", description = "评估不存在")
    })
    @PutMapping("/{assessmentId}")
    public Map<String, Object> updateAssessment(
            @Parameter(description = "评估ID") @PathVariable Integer assessmentId,
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

    @Operation(summary = "通过申请", description = "通过指定的领养申请")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "操作成功"),
            @ApiResponse(responseCode = "500", description = "操作失败")
    })
    @PutMapping("/{assessmentId}/approve")
    public Map<String, Object> approveAssessment(
            @Parameter(description = "评估ID") @PathVariable Integer assessmentId) {
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

    @Operation(summary = "拒绝申请", description = "拒绝指定的领养申请")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "操作成功"),
            @ApiResponse(responseCode = "500", description = "操作失败")
    })
    @PutMapping("/{assessmentId}/reject")
    public Map<String, Object> rejectAssessment(
            @Parameter(description = "评估ID") @PathVariable Integer assessmentId) {
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