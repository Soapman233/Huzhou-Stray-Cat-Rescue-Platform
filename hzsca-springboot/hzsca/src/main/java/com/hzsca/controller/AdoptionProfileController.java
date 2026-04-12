package com.hzsca.controller;

import com.hzsca.common.Result;
import com.hzsca.entity.UserAdoptionProfile;
import com.hzsca.mapper.UserAdoptionProfileMapper;
import com.hzsca.security.SecurityUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/adoption-profile")
public class AdoptionProfileController {
    private final UserAdoptionProfileMapper profileMapper;

    public AdoptionProfileController(UserAdoptionProfileMapper profileMapper) {
        this.profileMapper = profileMapper;
    }

    @GetMapping("/me")
    public Result getMyProfile() {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return Result.error("401", "未登录");
        }
        UserAdoptionProfile profile = profileMapper.selectByUserId(userId);
        Map<String, Object> data = new HashMap<>();
        data.put("profile", profile);
        data.put("completed", isCompleted(profile));
        return Result.success(data);
    }

    @PutMapping("/me")
    public Result upsertMyProfile(@RequestBody UserAdoptionProfile profile) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return Result.error("401", "未登录");
        }
        if (profile == null) {
            return Result.error("400", "缺少参数");
        }
        profile.setUserId(userId);
        normalize(profile);
        if (!isValidForSave(profile)) {
            return Result.error("400", "请完善必填项：城市、住房类型、每日可陪伴时长");
        }
        profileMapper.upsert(profile);
        return getMyProfile();
    }

    @DeleteMapping("/me")
    public Result deleteMyProfile() {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return Result.error("401", "未登录");
        }
        profileMapper.deleteByUserId(userId);
        return Result.success();
    }

    private static void normalize(UserAdoptionProfile p) {
        if (p.getHomeSizeLevel() == null) {
            p.setHomeSizeLevel(2);
        }
        if (p.getHomeSizeLevel() < 1) {
            p.setHomeSizeLevel(1);
        }
        if (p.getHomeSizeLevel() > 3) {
            p.setHomeSizeLevel(3);
        }
        if (p.getHasChildren() == null) {
            p.setHasChildren(false);
        }
        if (p.getHasOtherPets() == null) {
            p.setHasOtherPets(false);
        }
        if (p.getAllergy() == null) {
            p.setAllergy(false);
        }
        if (p.getMaxSheddingLevel() == null) {
            p.setMaxSheddingLevel(5);
        }
        if (p.getMaxSheddingLevel() < 1) {
            p.setMaxSheddingLevel(1);
        }
        if (p.getMaxSheddingLevel() > 5) {
            p.setMaxSheddingLevel(5);
        }
        if (p.getDailyCompanionshipHours() == null) {
            p.setDailyCompanionshipHours(0);
        }
        if (p.getDailyCompanionshipHours() < 0) {
            p.setDailyCompanionshipHours(0);
        }
        if (p.getDailyCompanionshipHours() > 24) {
            p.setDailyCompanionshipHours(24);
        }
        if (p.getWorkSchedule() == null || p.getWorkSchedule().isBlank()) {
            p.setWorkSchedule("DAY");
        }
        if (p.getBudgetLevel() == null) {
            p.setBudgetLevel(2);
        }
        if (p.getBudgetLevel() < 1) {
            p.setBudgetLevel(1);
        }
        if (p.getBudgetLevel() > 3) {
            p.setBudgetLevel(3);
        }
        if (p.getExperienceLevel() == null) {
            p.setExperienceLevel(0);
        }
        if (p.getExperienceLevel() < 0) {
            p.setExperienceLevel(0);
        }
        if (p.getExperienceLevel() > 2) {
            p.setExperienceLevel(2);
        }
        if (p.getPersonalityPreference() == null || p.getPersonalityPreference().isBlank()) {
            p.setPersonalityPreference("ANY");
        }
        if (p.getAcceptSpecialCare() == null) {
            p.setAcceptSpecialCare(false);
        }
    }

    private static boolean isValidForSave(UserAdoptionProfile p) {
        return p.getResidenceCity() != null && !p.getResidenceCity().isBlank()
                && p.getHousingType() != null && !p.getHousingType().isBlank()
                && p.getDailyCompanionshipHours() != null;
    }

    private static boolean isCompleted(UserAdoptionProfile p) {
        if (p == null) {
            return false;
        }
        return isValidForSave(p);
    }
}
