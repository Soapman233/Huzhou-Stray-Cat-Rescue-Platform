package com.hzsca.controller;

import com.hzsca.common.Result;
import com.hzsca.entity.Users;
import com.hzsca.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserMapper userMapper;

    @GetMapping("/current")
    public Result getCurrentUser(@RequestParam String username) {
        Users user = userMapper.selectByUsername(username);
        if (user == null) {
            return Result.error("500", "User not found");
        }
        // Don't return sensitive information like password
        user.setPassword(null);
        return Result.success(user);
    }

    @PutMapping("/update")
    public Result updateUser(@RequestBody Users user) {
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

    @DeleteMapping("/delete")
    public Result deleteUser(@RequestParam String username) {
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