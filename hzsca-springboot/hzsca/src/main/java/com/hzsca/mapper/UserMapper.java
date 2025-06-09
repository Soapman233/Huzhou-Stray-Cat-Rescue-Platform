package com.hzsca.mapper;

import com.hzsca.entity.Users;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserMapper {
    @Insert("INSERT INTO users (username, password, name, phone, gender, profile, avatar, role) " +
            "VALUES (#{username}, #{password}, #{name}, #{phone}, #{gender}, #{profile}, #{avatar}, #{role})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Users user);

    @Select("SELECT * FROM users WHERE username = #{username}")
    Users selectByUsername(String username);

    @Select("SELECT * FROM users WHERE id = #{id}")
    Users selectById(Integer id);

    @Update("UPDATE users SET password = #{password} WHERE id = #{id}")
    int updatePassword(@Param("id") Integer id, @Param("password") String password);

    @Update("UPDATE users SET phone = #{phone}, name = #{name}, " +
            "gender = #{gender}, profile = #{profile}, avatar = #{avatar} " +
            "WHERE id = #{id}")
    int updateProfile(Users user);
}