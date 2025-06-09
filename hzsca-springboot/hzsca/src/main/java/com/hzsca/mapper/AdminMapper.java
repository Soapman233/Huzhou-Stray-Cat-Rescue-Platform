package com.hzsca.mapper;

import com.hzsca.entity.Admin;
import org.apache.ibatis.annotations.*;

@Mapper
public interface AdminMapper {
    @Insert("INSERT INTO admin (username, password, name, role, avatar) " +
            "VALUES (#{username}, #{password}, #{name}, #{role}, #{avatar})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Admin admin);

    @Select("SELECT * FROM admin WHERE username = #{username}")
    Admin selectByUsername(String username);

    @Select("SELECT * FROM admin WHERE id = #{id}")
    Admin selectById(Integer id);

    @Update("UPDATE admin SET password = #{password} WHERE id = #{id}")
    int updatePassword(@Param("id") Integer id, @Param("password") String password);

    @Update("UPDATE admin SET name = #{name}, avatar = #{avatar} WHERE id = #{id}")
    int updateProfile(Admin admin);
}