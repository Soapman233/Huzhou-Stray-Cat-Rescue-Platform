package com.hzsca.service.impl;

import com.hzsca.entity.Users;
import com.hzsca.mapper.UserMapper;
import com.hzsca.service.UserService;
import com.hzsca.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RedisUtil redisUtil;

    // Redis key 前缀
    private static final String REDIS_USER_KEY_PREFIX = "user:";
    private static final long USER_CACHE_EXPIRE_TIME = 3600; // 1小时过期

    @Override
    public Users getUserByUsername(String username) {
        // 先从Redis缓存中获取
        String redisKey = REDIS_USER_KEY_PREFIX + username;
        Object cachedUser = redisUtil.get(redisKey);

        if (cachedUser != null) {
            return (Users) cachedUser;
        }

        // 缓存中没有，从数据库获取
        Users user = userMapper.selectByUsername(username);
        if (user != null) {
            // 存入Redis缓存
            redisUtil.set(redisKey, user, USER_CACHE_EXPIRE_TIME);
        }
        return user;
    }

    @Override
    public boolean updateProfile(Users user) {
        // 更新数据库
        boolean result = userMapper.updateProfile(user) > 0;
        if (result) {
            // 更新成功后，删除缓存
            String redisKey = REDIS_USER_KEY_PREFIX + user.getUsername();
            redisUtil.delete(redisKey);
        }
        return result;
    }

    @Override
    public boolean updatePassword(String username, String newPassword) {
        // 更新数据库
        boolean result = userMapper.updatePassword(username, newPassword) > 0;
        if (result) {
            // 更新成功后，删除缓存
            String redisKey = REDIS_USER_KEY_PREFIX + username;
            redisUtil.delete(redisKey);
        }
        return result;
    }

    // ... 其他方法也类似，在更新操作后删除缓存 ...
}