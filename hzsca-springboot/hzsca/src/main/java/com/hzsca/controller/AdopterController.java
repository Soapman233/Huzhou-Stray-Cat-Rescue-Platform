package com.hzsca.controller;

import com.hzsca.entity.Adopter;
import com.hzsca.mapper.AdopterMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class AdopterController {
    @Autowired
    private AdopterMapper adopterMapper;

    // 创建领养人信息
    @PostMapping("/adopters")
    public Map<String, Object> createAdopter(@RequestBody Adopter adopter) {
        Map<String, Object> result = new HashMap<>();

        // 检查身份证号是否已存在
        Adopter existingAdopter = adopterMapper.selectByIdCardNumber(adopter.getIdCardNumber());
        if (existingAdopter != null) {
            result.put("success", false);
            result.put("message", "该身份证号已注册");
            return result;
        }

        // 保存领养人信息
        adopterMapper.insert(adopter);

        result.put("success", true);
        result.put("message", "领养人信息创建成功");
        result.put("id", adopter.getAdopterId());
        return result;
    }

    // 根据ID获取领养人信息
    @GetMapping("/adopters/{id}")
    public Adopter getAdopter(@PathVariable Integer id) {
        return adopterMapper.selectById(id);
    }

    // 根据用户ID获取领养人信息
    @GetMapping("/adopters/user/{userId}")
    public Adopter getAdopterByUserId(@PathVariable Integer userId) {
        return adopterMapper.selectByUserId(userId);
    }
}