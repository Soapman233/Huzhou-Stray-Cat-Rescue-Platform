package com.hzsca.controller;

import com.hzsca.entity.Adopter;
import com.hzsca.mapper.AdopterMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api")
public class AdopterController {
    private static final Logger logger = LoggerFactory.getLogger(AdopterController.class);

    @Autowired
    private AdopterMapper adopterMapper;

    // 创建领养人信息
    @PostMapping("/adopters")
    public Map<String, Object> createAdopter(@RequestBody Adopter adopter) {
        Map<String, Object> result = new HashMap<>();

        try {
            logger.info("Creating adopter with data: {}", adopter);

            // 验证必填字段
            if (adopter.getUserId() == null || adopter.getName() == null ||
                    adopter.getIdCardNumber() == null || adopter.getPhone() == null ||
                    adopter.getAddress() == null) {
                result.put("success", false);
                result.put("message", "缺少必填信息");
                return result;
            }

            // 检查身份证号是否已存在
            Adopter existingAdopter = adopterMapper.selectByIdCardNumber(adopter.getIdCardNumber());
            if (existingAdopter != null) {
                result.put("success", false);
                result.put("message", "该身份证号已注册");
                return result;
            }

            // 检查用户是否已经是领养人
            Adopter existingUserAdopter = adopterMapper.selectByUserId(adopter.getUserId());
            if (existingUserAdopter != null) {
                result.put("success", false);
                result.put("message", "该用户已经注册为领养人");
                return result;
            }

            // 保存领养人信息
            int rows = adopterMapper.insert(adopter);
            if (rows > 0) {
                logger.info("Successfully created adopter with ID: {}", adopter.getAdopterId());
                result.put("success", true);
                result.put("message", "领养人信息创建成功");
                result.put("id", adopter.getAdopterId());
            } else {
                result.put("success", false);
                result.put("message", "创建领养人信息失败");
            }
        } catch (Exception e) {
            logger.error("Error creating adopter: ", e);
            result.put("success", false);
            result.put("message", "系统错误：" + e.getMessage());
        }

        return result;
    }

    // 根据ID获取领养人信息
    @GetMapping("/adopters/{id}")
    public Map<String, Object> getAdopter(@PathVariable Integer id) {
        Map<String, Object> result = new HashMap<>();
        try {
            Adopter adopter = adopterMapper.selectById(id);
            if (adopter != null) {
                result.put("success", true);
                result.put("data", adopter);
            } else {
                result.put("success", false);
                result.put("message", "未找到领养人信息");
            }
        } catch (Exception e) {
            logger.error("Error getting adopter: ", e);
            result.put("success", false);
            result.put("message", "系统错误：" + e.getMessage());
        }
        return result;
    }

    // 根据用户ID获取领养人信息
    @GetMapping("/adopters/user/{userId}")
    public Map<String, Object> getAdopterByUserId(@PathVariable Integer userId) {
        Map<String, Object> result = new HashMap<>();
        try {
            Adopter adopter = adopterMapper.selectByUserId(userId);
            if (adopter != null) {
                result.put("success", true);
                result.put("data", adopter);
            } else {
                result.put("success", false);
                result.put("message", "未找到领养人信息");
            }
        } catch (Exception e) {
            logger.error("Error getting adopter by user ID: ", e);
            result.put("success", false);
            result.put("message", "系统错误：" + e.getMessage());
        }
        return result;
    }
}