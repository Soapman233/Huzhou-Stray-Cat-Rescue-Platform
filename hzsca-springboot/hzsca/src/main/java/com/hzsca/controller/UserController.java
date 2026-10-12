package com.hzsca.controller;

import com.hzsca.common.Result;
import com.hzsca.entity.Users;
import com.hzsca.mapper.UserMapper;
import com.hzsca.security.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "用户管理", description = "用户信息的增删改查接口")
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserMapper userMapper;

    /**
     * 当前登录者是否为管理员。
     */
    private boolean isAdmin() {
        String role = SecurityUtils.getCurrentRole();
        return role != null && role.equalsIgnoreCase("ROLE_admin");
    }

    /**
     * 校验当前登录者是否有权操作该用户名对应的账号：管理员放行，普通用户仅能操作自己。
     * 用于防止通过修改 username 参数越权查看/修改/删除他人资料（IDOR）。
     */
    private boolean isSelfOrAdmin(String username) {
        if (isAdmin()) {
            return true;
        }
        Long currentId = SecurityUtils.getCurrentUserId();
        if (currentId == null || username == null) {
            return false;
        }
        Users target = userMapper.selectByUsername(username);
        return target != null && target.getUserID() != null && target.getUserID().longValue() == currentId;
    }

    @Operation(summary = "获取所有用户", description = "获取系统中所有用户的列表")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "获取成功"),
            @ApiResponse(responseCode = "500", description = "服务器错误")
    })
    @GetMapping
    public Result getAllUsers() {
        try {
            List<Users> users = userMapper.selectAll();
            // 移除敏感信息
            users.forEach(user -> user.setPassword(null));
            return Result.success(users);
        } catch (Exception e) {
            return Result.error("500", "获取用户列表失败: " + e.getMessage());
        }
    }

    @Operation(summary = "获取当前用户", description = "根据用户名获取用户信息")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "获取成功"),
            @ApiResponse(responseCode = "500", description = "用户不存在")
    })
    @GetMapping("/current")
    public Result getCurrentUser(@Parameter(description = "用户名") @RequestParam String username) {
        if (!isSelfOrAdmin(username)) {
            return Result.error("403", "无权查看其他用户信息");
        }
        Users user = userMapper.selectByUsername(username);
        if (user == null) {
            return Result.error("500", "User not found");
        }
        // Don't return sensitive information like password
        user.setPassword(null);
        return Result.success(user);
    }

    @Operation(summary = "更新用户信息", description = "更新指定用户的个人信息")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "更新成功"),
            @ApiResponse(responseCode = "500", description = "用户不存在或更新失败")
    })
    @PutMapping("/update")
    public Result updateUser(@RequestBody Users user) {
        if (!isSelfOrAdmin(user.getUsername())) {
            return Result.error("403", "无权修改其他用户信息");
        }
        Users existingUser = userMapper.selectByUsername(user.getUsername());
        if (existingUser == null) {
            return Result.error("500", "User not found");
        }

        // Set the UserID from the existing user
        user.setUserID(existingUser.getUserID());

        // Keep existing password and role
        user.setPassword(existingUser.getPassword());
        user.setRole(existingUser.getRole());

        int rows = userMapper.updateProfile(user);
        if (rows > 0) {
            // Return updated user info
            Users updatedUser = userMapper.selectByUsername(user.getUsername());
            updatedUser.setPassword(null);
            return Result.success(updatedUser);
        }
        return Result.error("500", "Failed to update user information");
    }

    @Operation(summary = "删除用户", description = "根据用户名删除用户")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "删除成功"),
            @ApiResponse(responseCode = "500", description = "用户不存在或删除失败")
    })
    @DeleteMapping("/delete")
    public Result deleteUser(@Parameter(description = "用户名") @RequestParam String username) {
        if (!isSelfOrAdmin(username)) {
            return Result.error("403", "无权删除其他用户");
        }
        Users existingUser = userMapper.selectByUsername(username);
        if (existingUser == null) {
            return Result.error("500", "User not found");
        }

        int rows = userMapper.deleteByUsername(username);
        if (rows > 0) {
            return Result.success();
        }
        return Result.error("500", "Failed to delete user");
    }
}