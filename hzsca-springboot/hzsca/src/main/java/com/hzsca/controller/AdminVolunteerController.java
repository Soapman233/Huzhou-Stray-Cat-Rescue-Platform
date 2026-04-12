package com.hzsca.controller;

import com.hzsca.common.Result;
import com.hzsca.entity.ActivityParticipation;
import com.hzsca.entity.Users;
import com.hzsca.entity.VolunteerApplication;
import com.hzsca.entity.WxUser;
import com.hzsca.mapper.ActivityParticipationMapper;
import com.hzsca.mapper.UserMapper;
import com.hzsca.mapper.VolunteerApplicationMapper;
import com.hzsca.mapper.WxUserMapper;
import com.hzsca.security.SecurityUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/volunteers")
public class AdminVolunteerController {
    private final VolunteerApplicationMapper volunteerApplicationMapper;
    private final UserMapper userMapper;
    private final WxUserMapper wxUserMapper;
    private final ActivityParticipationMapper activityParticipationMapper;

    public AdminVolunteerController(
            VolunteerApplicationMapper volunteerApplicationMapper,
            UserMapper userMapper,
            WxUserMapper wxUserMapper,
            ActivityParticipationMapper activityParticipationMapper
    ) {
        this.volunteerApplicationMapper = volunteerApplicationMapper;
        this.userMapper = userMapper;
        this.wxUserMapper = wxUserMapper;
        this.activityParticipationMapper = activityParticipationMapper;
    }

    @GetMapping("/applications")
    public Result applications(
            @RequestParam(required = false) String status,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "20") Integer size
    ) {
        int p = page == null || page < 1 ? 1 : page;
        int s = size == null || size < 1 ? 20 : Math.min(size, 200);
        int offset = (p - 1) * s;
        List<VolunteerApplication> records = volunteerApplicationMapper.selectPage(status, offset, s);
        int total = volunteerApplicationMapper.count(status);
        Map<String, Object> data = new HashMap<>();
        data.put("records", records);
        data.put("total", total);
        data.put("page", p);
        data.put("size", s);
        return Result.success(data);
    }

    @PostMapping("/applications/{id}/approve")
    public Result approve(@PathVariable Long id, @RequestParam(required = false) String reason) {
        Long adminId = SecurityUtils.getCurrentUserId();
        VolunteerApplication app = volunteerApplicationMapper.selectById(id);
        if (app == null) {
            return Result.error("404", "申请不存在");
        }
        volunteerApplicationMapper.updateStatus(id, "APPROVED", adminId, reason == null ? "通过审核" : reason);
        setVolunteerRole(app);
        return Result.success();
    }

    @PostMapping("/applications/{id}/reject")
    public Result reject(@PathVariable Long id, @RequestParam(required = false) String reason) {
        Long adminId = SecurityUtils.getCurrentUserId();
        VolunteerApplication app = volunteerApplicationMapper.selectById(id);
        if (app == null) {
            return Result.error("404", "申请不存在");
        }
        volunteerApplicationMapper.updateStatus(id, "REJECTED", adminId, reason == null ? "未通过审核" : reason);
        return Result.success();
    }

    @GetMapping("/activities/{activityId}/participants")
    public Result participants(@PathVariable Integer activityId, @RequestParam(required = false) String status) {
        if (activityId == null) {
            return Result.error("400", "缺少活动ID");
        }
        List<ActivityParticipation> list = activityParticipationMapper.selectByActivity(activityId, status);
        List<Map<String, Object>> out = new ArrayList<>();
        for (ActivityParticipation p : list) {
            if (p == null) continue;
            Map<String, Object> item = new HashMap<>();
            item.put("id", p.getId());
            item.put("activityId", p.getActivityId());
            item.put("participantType", p.getParticipantType());
            item.put("participantId", p.getParticipantId());
            item.put("status", p.getStatus());
            item.put("joinTime", p.getJoinTime());
            item.put("auditTime", p.getAuditTime());
            item.put("auditorId", p.getAuditorId());
            item.put("auditReason", p.getAuditReason());

            if ("WEB".equalsIgnoreCase(p.getParticipantType())) {
                Users u = userMapper.selectById(p.getParticipantId() == null ? null : p.getParticipantId().intValue());
                if (u != null) {
                    u.setPassword(null);
                    item.put("user", u);
                }
            } else if ("WX".equalsIgnoreCase(p.getParticipantType())) {
                WxUser u = wxUserMapper.selectById(p.getParticipantId());
                if (u != null) {
                    item.put("user", u);
                }
            }
            out.add(item);
        }
        Map<String, Object> data = new HashMap<>();
        data.put("records", out);
        data.put("total", out.size());
        return Result.success(data);
    }

    @PostMapping("/participations/{id}/approve")
    public Result approveParticipation(@PathVariable Long id, @RequestParam(required = false) String reason) {
        Long adminId = SecurityUtils.getCurrentUserId();
        if (id == null) {
            return Result.error("400", "缺少ID");
        }
        activityParticipationMapper.updateStatus(id, "APPROVED", adminId, reason == null ? "通过审核" : reason);
        return Result.success();
    }

    @PostMapping("/participations/{id}/reject")
    public Result rejectParticipation(@PathVariable Long id, @RequestParam(required = false) String reason) {
        Long adminId = SecurityUtils.getCurrentUserId();
        if (id == null) {
            return Result.error("400", "缺少ID");
        }
        activityParticipationMapper.updateStatus(id, "REJECTED", adminId, reason == null ? "未通过审核" : reason);
        return Result.success();
    }

    private void setVolunteerRole(VolunteerApplication app) {
        String type = app.getApplicantType();
        Long id = app.getApplicantId();
        if (type == null || id == null) {
            return;
        }
        if ("WEB".equalsIgnoreCase(type)) {
            userMapper.updateRoleById(id.intValue(), "volunteer");
        } else if ("WX".equalsIgnoreCase(type)) {
            wxUserMapper.updateRole(id, "volunteer");
        }
    }
}
