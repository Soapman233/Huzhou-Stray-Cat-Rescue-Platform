package com.hzsca.mapper;

import com.hzsca.entity.Admin;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AdminMapper {
    int insert(Admin admin);
    Admin selectByUsername(String username);
    Admin selectById(Integer id);
    int update(Admin admin);
    int deleteById(Integer id);
    int updatePassword(@Param("id") Integer id, @Param("password") String password);
    int updateProfile(Admin admin);
}