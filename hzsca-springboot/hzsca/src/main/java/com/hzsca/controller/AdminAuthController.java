package com.hzsca.controller;

import com.hzsca.common.Result;
import com.hzsca.entity.Admin;
import com.hzsca.mapper.AdminMapper;
import com.hzsca.security.JwtService;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminAuthController {
    private final AdminMapper adminMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AdminAuthController(AdminMapper adminMapper, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.adminMapper = adminMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Result login(@RequestBody AdminLoginRequest request) {
        if (request == null || request.username == null || request.username.isBlank()
                || request.password == null || request.password.isBlank()) {
            return Result.error("400", "缺少用户名或密码");
        }

        Admin admin = adminMapper.selectByUsername(request.username);
        if (admin == null || admin.getPassword() == null) {
            return Result.error("401", "用户名或密码错误");
        }

        String stored = admin.getPassword();
        boolean ok;
        if (stored.startsWith("$2a$") || stored.startsWith("$2b$") || stored.startsWith("$2y$")) {
            ok = passwordEncoder.matches(request.password, stored);
        } else {
            ok = stored.equals(request.password);
            if (ok) {
                adminMapper.updatePassword(admin.getId(), passwordEncoder.encode(request.password));
                admin = adminMapper.selectByUsername(request.username);
            }
        }

        if (!ok) {
            return Result.error("401", "用户名或密码错误");
        }

        if (admin == null || admin.getId() == null) {
            return Result.error("500", "登录失败");
        }

        String token = jwtService.generateToken(admin.getId(), "admin", "admin");
        admin.setPassword(null);
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("role", "admin");
        data.put("user", admin);
        return Result.success(data);
    }

    public static class AdminLoginRequest {
        public String username;
        public String password;
    }
}
