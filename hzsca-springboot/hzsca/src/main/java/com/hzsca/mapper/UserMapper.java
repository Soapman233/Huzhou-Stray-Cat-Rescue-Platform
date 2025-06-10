package com.hzsca.mapper;

import com.hzsca.entity.Users;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserMapper {
    @Insert("INSERT INTO Users (Username, Password, name, Phone, Gender, Profile, avatar, role) " +
            "VALUES (#{username}, #{password}, #{name}, #{phone}, #{gender}, #{profile}, #{avatar}, #{role})")
    @Options(useGeneratedKeys = true, keyProperty = "UserID")
    int insert(Users user);

    @Select("SELECT * FROM Users WHERE Username = #{username}")
    Users selectByUsername(String username);

    @Select("SELECT * FROM Users WHERE UserID = #{UserID}")
    Users selectById(Integer UserID);

    @Update("UPDATE Users SET Password = #{password} WHERE Username = #{username}")
    int updatePassword(@Param("username") String username, @Param("password") String password);

    @Update("UPDATE Users SET Phone = #{phone}, name = #{name}, " +
            "Gender = #{gender}, Profile = #{profile}, avatar = #{avatar} " +
            "WHERE Username = #{username}")
    int updateProfile(Users user);

    @Delete("DELETE FROM Users WHERE Username = #{username}")
    int deleteByUsername(String username);
}