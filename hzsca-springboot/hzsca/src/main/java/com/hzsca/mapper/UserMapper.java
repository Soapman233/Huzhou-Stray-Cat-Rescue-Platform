package com.hzsca.mapper;

import com.hzsca.entity.Users;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface UserMapper {
    /**
     * 插入新用户
     */
    int insert(Users user);

    /**
     * 根据用户名查询用户
     */
    Users selectByUsername(String username);

    /**
     * 根据ID查询用户
     */
    Users selectById(Integer UserID);

    /**
     * 更新用户密码
     */
    int updatePassword(@Param("username") String username, @Param("password") String password);

    /**
     * 更新用户信息
     */
    int updateProfile(Users user);

    /**
     * 删除用户
     */
    int deleteByUsername(String username);

    /**
     * 获取所有用户
     */
    List<Users> selectAll();
}