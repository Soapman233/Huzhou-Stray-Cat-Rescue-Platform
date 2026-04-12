package com.hzsca.controller;

import com.hzsca.common.Result;
import com.hzsca.entity.WxUser;
import com.hzsca.mapper.WxUserMapper;
import com.hzsca.security.SecurityUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/wx")
public class WxUserController {
    private final WxUserMapper wxUserMapper;

    public WxUserController(WxUserMapper wxUserMapper) {
        this.wxUserMapper = wxUserMapper;
    }

    @GetMapping("/me")
    public Result me() {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return Result.error("401", "未登录");
        }
        WxUser user = wxUserMapper.selectById(userId);
        return Result.success(user);
    }

    @PutMapping("/me")
    public Result updateMe(@RequestBody WxUser update) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return Result.error("401", "未登录");
        }
        if (update == null) {
            return Result.error("400", "缺少参数");
        }
        WxUser existing = wxUserMapper.selectById(userId);
        if (existing == null) {
            return Result.error("404", "用户不存在");
        }
        WxUser patch = new WxUser();
        patch.setId(userId);
        patch.setNickname(update.getNickname());
        patch.setAvatar(update.getAvatar());
        patch.setGender(update.getGender());
        patch.setCity(update.getCity());
        patch.setProvince(update.getProvince());
        patch.setCountry(update.getCountry());
        wxUserMapper.touchAndUpdateProfile(patch);
        return me();
    }
}
