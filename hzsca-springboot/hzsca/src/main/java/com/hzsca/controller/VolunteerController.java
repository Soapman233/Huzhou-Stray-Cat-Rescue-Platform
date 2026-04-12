package com.hzsca.controller;

import com.hzsca.common.Result;
import com.hzsca.entity.VolunteerApplication;
import com.hzsca.mapper.VolunteerApplicationMapper;
import com.hzsca.security.SecurityUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/volunteer")
public class VolunteerController {
    private final VolunteerApplicationMapper volunteerApplicationMapper;

    public VolunteerController(VolunteerApplicationMapper volunteerApplicationMapper) {
        this.volunteerApplicationMapper = volunteerApplicationMapper;
    }

    @GetMapping("/me")
    public Result myApplication() {
        Long userId = SecurityUtils.getCurrentUserId();
        String type = SecurityUtils.getCurrentPrincipalType();
        if (userId == null || type == null) {
            return Result.error("401", "未登录");
        }
        VolunteerApplication latest = volunteerApplicationMapper.selectLatestByApplicant(type.toUpperCase(), userId);
        Map<String, Object> data = new HashMap<>();
        data.put("latest", latest);
        data.put("role", (SecurityUtils.getCurrentRole() == null ? null : SecurityUtils.getCurrentRole().replace("ROLE_", "")));
        return Result.success(data);
    }

    @PostMapping("/apply")
    public Result apply(@RequestBody VolunteerApplication body) {
        Long userId = SecurityUtils.getCurrentUserId();
        String type = SecurityUtils.getCurrentPrincipalType();
        if (userId == null || type == null) {
            return Result.error("401", "未登录");
        }
        if (body == null || body.getName() == null || body.getName().isBlank()) {
            return Result.error("400", "请填写姓名");
        }
        VolunteerApplication latest = volunteerApplicationMapper.selectLatestByApplicant(type.toUpperCase(), userId);
        if (latest != null && "PENDING".equalsIgnoreCase(latest.getStatus())) {
            return Result.error("400", "已有待审核申请");
        }
        VolunteerApplication app = new VolunteerApplication();
        app.setApplicantType(type.toUpperCase());
        app.setApplicantId(userId);
        app.setName(body.getName());
        app.setPhone(body.getPhone());
        app.setAge(body.getAge());
        app.setExperience(body.getExperience());
        app.setMotivation(body.getMotivation());
        app.setStatus("PENDING");
        volunteerApplicationMapper.insert(app);
        return Result.success(app);
    }
}
