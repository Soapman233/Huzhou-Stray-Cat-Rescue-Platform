package com.hzsca.mapper;

import com.hzsca.entity.WxUser;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface WxUserMapper {
    @Select("SELECT id, openid, unionid, nickname, avatar, role, gender, city, province, country, create_time AS createTime, last_login_time AS lastLoginTime " +
            "FROM wx_user WHERE id = #{id}")
    WxUser selectById(@Param("id") Long id);

    @Select("SELECT id, openid, unionid, nickname, avatar, role, gender, city, province, country, create_time AS createTime, last_login_time AS lastLoginTime " +
            "FROM wx_user WHERE openid = #{openid}")
    WxUser selectByOpenid(@Param("openid") String openid);

    @Insert("INSERT INTO wx_user (openid, unionid, nickname, avatar, role, gender, city, province, country, last_login_time) " +
            "VALUES (#{openid}, #{unionid}, #{nickname}, #{avatar}, COALESCE(#{role}, 'user'), #{gender}, #{city}, #{province}, #{country}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(WxUser user);

    @Update("UPDATE wx_user SET " +
            "unionid = COALESCE(#{unionid}, unionid), " +
            "nickname = COALESCE(#{nickname}, nickname), " +
            "avatar = COALESCE(#{avatar}, avatar), " +
            "gender = COALESCE(#{gender}, gender), " +
            "city = COALESCE(#{city}, city), " +
            "province = COALESCE(#{province}, province), " +
            "country = COALESCE(#{country}, country), " +
            "last_login_time = NOW() " +
            "WHERE id = #{id}")
    int touchAndUpdateProfile(WxUser user);

    @Update("UPDATE wx_user SET role = #{role} WHERE id = #{id}")
    int updateRole(@Param("id") Long id, @Param("role") String role);
}
