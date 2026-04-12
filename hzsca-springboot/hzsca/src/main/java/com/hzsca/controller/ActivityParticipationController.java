package com.hzsca.controller;

import com.hzsca.common.Result;
import com.hzsca.entity.ActivityParticipation;
import com.hzsca.mapper.ActivityParticipationMapper;
import com.hzsca.security.SecurityUtils;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/activity")
public class ActivityParticipationController {
    private final ActivityParticipationMapper activityParticipationMapper;

    public ActivityParticipationController(ActivityParticipationMapper activityParticipationMapper) {
        this.activityParticipationMapper = activityParticipationMapper;
    }

    @PostMapping("/join/{activityId}")
    public Result join(@PathVariable Integer activityId) {
        Long userId = SecurityUtils.getCurrentUserId();
        String type = SecurityUtils.getCurrentPrincipalType();
        String role = SecurityUtils.getCurrentRole();
        if (userId == null || type == null) {
            return Result.error("401", "未登录");
        }
        if (role == null || !(role.contains("ROLE_volunteer") || role.contains("ROLE_admin"))) {
            return Result.error("403", "需要志愿者身份");
        }
        if (activityId == null) {
            return Result.error("400", "缺少活动ID");
        }
        ActivityParticipation p = new ActivityParticipation();
        p.setActivityId(activityId);
        p.setParticipantType(type.toUpperCase());
        p.setParticipantId(userId);
        p.setStatus("PENDING");
        try {
            activityParticipationMapper.insert(p);
        } catch (DuplicateKeyException e) {
            return Result.success();
        }
        Map<String, Object> data = new HashMap<>();
        data.put("status", "PENDING");
        return Result.success(data);
    }

    @GetMapping("/join/{activityId}/me")
    public Result myJoinStatus(@PathVariable Integer activityId) {
        Long userId = SecurityUtils.getCurrentUserId();
        String type = SecurityUtils.getCurrentPrincipalType();
        if (userId == null || type == null) {
            return Result.error("401", "未登录");
        }
        ActivityParticipation p = activityParticipationMapper.selectByUnique(activityId, type.toUpperCase(), userId);
        Map<String, Object> data = new HashMap<>();
        data.put("record", p);
        return Result.success(data);
    }
}
