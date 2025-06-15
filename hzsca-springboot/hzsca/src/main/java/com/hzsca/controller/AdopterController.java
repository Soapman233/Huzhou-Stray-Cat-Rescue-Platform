package com.hzsca.controller;

import com.hzsca.entity.Adopter;
import com.hzsca.mapper.AdopterMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Tag(name = "领养人管理", description = "领养人信息的增删改查接口")
@RestController
@RequestMapping("/api")
public class AdopterController {
    private static final Logger logger = LoggerFactory.getLogger(AdopterController.class);

    @Autowired
    private AdopterMapper adopterMapper;

    @Operation(summary = "创建领养人", description = "创建新的领养人信息")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "创建成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "409", description = "领养人信息已存在")
    })
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

    @Operation(summary = "获取领养人", description = "根据ID获取领养人信息")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "获取成功"),
            @ApiResponse(responseCode = "404", description = "领养人不存在")
    })
    @GetMapping("/adopters/{id}")
    public Map<String, Object> getAdopter(@Parameter(description = "领养人ID") @PathVariable Integer id) {
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

    @Operation(summary = "根据用户ID获取领养人", description = "根据用户ID获取对应的领养人信息")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "获取成功"),
            @ApiResponse(responseCode = "404", description = "领养人不存在")
    })
    @GetMapping("/adopters/user/{userId}")
    public Map<String, Object> getAdopterByUserId(@Parameter(description = "用户ID") @PathVariable Integer userId) {
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