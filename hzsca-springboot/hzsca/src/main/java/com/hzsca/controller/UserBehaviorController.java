package com.hzsca.controller;

import com.hzsca.common.Result;
import com.hzsca.entity.UserBehavior;
import com.hzsca.mapper.UserBehaviorMapper;
import com.hzsca.security.SecurityUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/behaviors")
public class UserBehaviorController {
    private final UserBehaviorMapper userBehaviorMapper;

    public UserBehaviorController(UserBehaviorMapper userBehaviorMapper) {
        this.userBehaviorMapper = userBehaviorMapper;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public Result record(@RequestBody RecordRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return Result.error("401", "未登录");
        }
        if (request == null || request.event == null || request.event.isBlank()) {
            return Result.error("400", "缺少 event");
        }
        UserBehavior behavior = new UserBehavior();
        behavior.setWxUserId(userId);
        behavior.setEvent(request.event);
        behavior.setAnimalId(request.animalId);
        behavior.setRequestId(request.requestId);
        behavior.setReportId(request.reportId);
        behavior.setExtra(request.extra);
        userBehaviorMapper.insert(behavior);
        Map<String, Object> data = new HashMap<>();
        data.put("behaviorId", behavior.getId());
        return Result.success(data);
    }

    public static class RecordRequest {
        public String event;
        public Integer animalId;
        public Long requestId;
        public Long reportId;
        public String extra;
    }
}

