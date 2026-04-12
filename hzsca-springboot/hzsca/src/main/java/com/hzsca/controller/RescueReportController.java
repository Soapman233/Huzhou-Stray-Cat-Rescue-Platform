package com.hzsca.controller;

import com.hzsca.common.Result;
import com.hzsca.entity.RescueReport;
import com.hzsca.entity.UserBehavior;
import com.hzsca.mapper.RescueReportMapper;
import com.hzsca.mapper.UserBehaviorMapper;
import com.hzsca.security.SecurityUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/rescue-reports")
public class RescueReportController {
    private final RescueReportMapper rescueReportMapper;
    private final UserBehaviorMapper userBehaviorMapper;

    public RescueReportController(RescueReportMapper rescueReportMapper, UserBehaviorMapper userBehaviorMapper) {
        this.rescueReportMapper = rescueReportMapper;
        this.userBehaviorMapper = userBehaviorMapper;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public Result create(@RequestBody CreateRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        String principalType = SecurityUtils.getCurrentPrincipalType();
        if (userId == null) {
            return Result.error("401", "未登录");
        }
        RescueReport report = new RescueReport();
        String type = normalize(principalType);
        report.setReporterType(type);
        report.setReporterId(userId);
        report.setWxUserId("WX".equals(type) ? userId : null);
        if (request != null) {
            report.setLat(request.lat);
            report.setLng(request.lng);
            report.setAddress(request.address);
            report.setDescription(request.description);
            report.setImageUrls(request.imageUrls);
            report.setUrgency(request.urgency == null ? 1 : request.urgency);
        } else {
            report.setUrgency(1);
        }
        report.setStatus("OPEN");
        rescueReportMapper.insert(report);
        if (report.getId() != null && report.getWxUserId() != null) {
            UserBehavior behavior = new UserBehavior();
            behavior.setWxUserId(report.getWxUserId());
            behavior.setEvent("rescue_report");
            behavior.setReportId(report.getId());
            userBehaviorMapper.insert(behavior);
        }
        Map<String, Object> data = new HashMap<>();
        data.put("reportId", report.getId());
        return Result.success(data);
    }

    @GetMapping("/my")
    public Result my() {
        Long userId = SecurityUtils.getCurrentUserId();
        String principalType = SecurityUtils.getCurrentPrincipalType();
        if (userId == null) {
            return Result.error("401", "未登录");
        }
        List<RescueReport> list = rescueReportMapper.selectByReporter(normalize(principalType), userId);
        return Result.success(list);
    }

    public static class CreateRequest {
        public Double lat;
        public Double lng;
        public String address;
        public String description;
        public String imageUrls;
        public Integer urgency;
    }

    private static String normalize(String t) {
        if (t == null) {
            return "WX";
        }
        if ("wx".equalsIgnoreCase(t) || "WX".equalsIgnoreCase(t)) {
            return "WX";
        }
        if ("web".equalsIgnoreCase(t) || "WEB".equalsIgnoreCase(t)) {
            return "WEB";
        }
        if (t.isBlank() || "unknown".equalsIgnoreCase(t)) {
            return "WX";
        }
        return t.toUpperCase();
    }
}

