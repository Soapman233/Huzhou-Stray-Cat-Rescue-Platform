package com.hzsca.controller;

import com.hzsca.entity.LoginRequest;
import com.hzsca.entity.RegisterRequest;
import com.hzsca.entity.Users;
import com.hzsca.entity.Admin;
import com.hzsca.mapper.UserMapper;
import com.hzsca.mapper.AdminMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Tag(name = "认证管理", description = "用户登录、注册等认证相关接口")
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private AdminMapper adminMapper;

    @Operation(summary = "用户登录", description = "处理用户和管理员的登录请求")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "登录成功"),
            @ApiResponse(responseCode = "401", description = "用户名或密码错误")
    })
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody LoginRequest request) {
        Map<String, Object> result = new HashMap<>();
        logger.info("Login attempt for username: {}, role: {}", request.getUsername(), request.getRole());

        if ("user".equals(request.getRole())) {
            Users user = userMapper.selectByUsername(request.getUsername());
            if (user != null && user.getPassword().equals(request.getPassword())) {
                result.put("success", true);
                result.put("message", "登录成功");
                user.setPassword(null); // Don't send password back to client
                result.put("user", user);
                result.put("role", "user");
                logger.info("User login successful for: {}", request.getUsername());
            } else {
                result.put("success", false);
                result.put("message", "用户名或密码错误");
                logger.warn("User login failed for: {}", request.getUsername());
            }
        } else if ("admin".equals(request.getRole())) {
            Admin admin = adminMapper.selectByUsername(request.getUsername());
            logger.info("Admin lookup result: {}", admin != null ? "found" : "not found");

            if (admin != null && admin.getPassword().equals(request.getPassword())) {
                result.put("success", true);
                result.put("message", "登录成功");
                admin.setPassword(null); // Don't send password back to client
                result.put("user", admin);
                result.put("role", "admin");
                logger.info("Admin login successful for: {}", request.getUsername());
            } else {
                result.put("success", false);
                result.put("message", "用户名或密码错误");
                logger.warn("Admin login failed for: {}", request.getUsername());
            }
        } else {
            result.put("success", false);
            result.put("message", "无效的角色类型");
            logger.warn("Invalid role type attempted: {}", request.getRole());
        }

        return result;
    }

    @Operation(summary = "用户注册", description = "处理新用户的注册请求")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "注册成功"),
            @ApiResponse(responseCode = "400", description = "用户名已存在")
    })
    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody RegisterRequest request) {
        Map<String, Object> result = new HashMap<>();

        // 检查用户名是否已存在
        Users existingUser = userMapper.selectByUsername(request.getUsername());
        if (existingUser != null) {
            result.put("success", false);
            result.put("message", "用户名已存在");
            return result;
        }

        // 创建新用户
        Users newUser = new Users();
        newUser.setUsername(request.getUsername());
        newUser.setPassword(request.getPassword());
        newUser.setName(request.getName());
        newUser.setPhone(request.getPhone());
        newUser.setGender(Integer.parseInt(request.getGender())); // 将String转换为Integer
        newUser.setProfile(request.getProfile());
        newUser.setAvatar(request.getAvatar());
        newUser.setRole("user");  // 默认角色为普通用户

        // 保存用户信息
        userMapper.insert(newUser);

        result.put("success", true);
        result.put("message", "注册成功");
        newUser.setPassword(null); // Don't send password back to client
        result.put("user", newUser);
        return result;
    }

    @Operation(summary = "检查用户名", description = "检查用户名是否已被使用")
    @PostMapping("/check-username")
    public Map<String, Object> checkUsername(@RequestBody Map<String, String> request) {
        Map<String, Object> result = new HashMap<>();
        String username = request.get("username");

        Users user = userMapper.selectByUsername(username);
        Admin admin = adminMapper.selectByUsername(username);

        result.put("exists", user != null || admin != null);
        return result;
    }
}