package com.hzsca.controller;

import com.hzsca.entity.LoginRequest;
import com.hzsca.entity.RegisterRequest;
import com.hzsca.entity.Users;
import com.hzsca.entity.Admin;
import com.hzsca.mapper.UserMapper;
import com.hzsca.mapper.AdminMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private AdminMapper adminMapper;

    // 登录
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody LoginRequest request) {
        Map<String, Object> result = new HashMap<>();

        if ("user".equals(request.getRole())) {
            Users user = userMapper.selectByUsername(request.getUsername());
            if (user != null && user.getPassword().equals(request.getPassword())) {
                result.put("success", true);
                result.put("message", "登录成功");
                user.setPassword(null);
                result.put("user", user);
                result.put("role", "user");
            } else {
                result.put("success", false);
                result.put("message", "用户名或密码错误");
            }
        } else if ("admin".equals(request.getRole())) {
            Admin admin = adminMapper.selectByUsername(request.getUsername());
            if (admin != null && admin.getPassword().equals(request.getPassword())) {
                result.put("success", true);
                result.put("message", "登录成功");
                admin.setPassword(null); // Don't send password back to client
                result.put("user", admin);
                result.put("role", "admin");
            } else {
                result.put("success", false);
                result.put("message", "用户名或密码错误");
            }
        } else {
            result.put("success", false);
            result.put("message", "无效的角色类型");
        }

        return result;
    }

    // 注册
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
        newUser.setGender(Integer.parseInt(request.getGender()));
        newUser.setProfile(request.getProfile());
        newUser.setAvatar(request.getAvatar());
        newUser.setRole("user");

        // 保存用户信息
        userMapper.insert(newUser);

        result.put("success", true);
        result.put("message", "注册成功");
        newUser.setPassword(null);
        result.put("user", newUser);
        return result;
    }

    // 检查用户名是否存在
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