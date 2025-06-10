package com.hzsca.service;

import com.hzsca.entity.Users;

public interface UserService {
    /**
     * 根据用户名获取用户信息
     */
    Users getUserByUsername(String username);

    /**
     * 更新用户信息
     */
    boolean updateProfile(Users user);

    /**
     * 更新用户密码
     */
    boolean updatePassword(String username, String newPassword);
}