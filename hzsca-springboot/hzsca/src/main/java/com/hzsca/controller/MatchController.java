package com.hzsca.controller;

import com.hzsca.common.Result;
import com.hzsca.entity.UserAdoptionProfile;
import com.hzsca.mapper.UserAdoptionProfileMapper;
import com.hzsca.security.SecurityUtils;
import com.hzsca.service.MatchService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/match")
public class MatchController {
    private final MatchService matchService;
    private final UserAdoptionProfileMapper profileMapper;

    public MatchController(MatchService matchService, UserAdoptionProfileMapper profileMapper) {
        this.matchService = matchService;
        this.profileMapper = profileMapper;
    }

    @GetMapping("/recommendations")
    public Result recommendations(@RequestParam(required = false, defaultValue = "10") Integer limit) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return Result.error("401", "未登录");
        }
        UserAdoptionProfile profile = profileMapper.selectByUserId(userId);
        if (profile == null
                || profile.getResidenceCity() == null || profile.getResidenceCity().isBlank()
                || profile.getHousingType() == null || profile.getHousingType().isBlank()
                || profile.getDailyCompanionshipHours() == null) {
            return Result.error("400", "请先完善领养档案");
        }
        int l = limit == null ? 10 : limit;
        List<MatchService.Recommendation> list = matchService.recommend(userId, l);
        return Result.success(list);
    }
}
