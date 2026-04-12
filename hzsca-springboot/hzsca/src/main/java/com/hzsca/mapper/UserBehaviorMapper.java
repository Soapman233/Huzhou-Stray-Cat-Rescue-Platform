package com.hzsca.mapper;

import com.hzsca.entity.UserBehavior;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface UserBehaviorMapper {
    @Insert("INSERT INTO user_behavior (wx_user_id, event, animal_id, request_id, report_id, extra) " +
            "VALUES (#{wxUserId}, #{event}, #{animalId}, #{requestId}, #{reportId}, #{extra})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(UserBehavior behavior);

    @Select("<script>" +
            "SELECT b.id, b.wx_user_id AS wxUserId, b.event, b.animal_id AS animalId, b.request_id AS requestId, b.report_id AS reportId, b.extra, b.create_time AS createTime " +
            "FROM user_behavior b " +
            "<where>" +
            "<if test='wxUserId != null'> AND b.wx_user_id = #{wxUserId} </if>" +
            "<if test='event != null and event != \"\"'> AND b.event = #{event} </if>" +
            "</where>" +
            "ORDER BY b.id DESC" +
            "</script>")
    List<UserBehavior> selectAll(@Param("wxUserId") Long wxUserId, @Param("event") String event);

    @Select("<script>" +
            "SELECT b.id AS behaviorId, b.event, b.create_time AS createTime, b.extra, " +
            "b.wx_user_id AS wxUserId, u.nickname AS wxNickname, u.avatar AS wxAvatar, " +
            "b.animal_id AS animalId, a.Name AS animalName, " +
            "b.request_id AS requestId, r.status AS requestStatus, " +
            "b.report_id AS reportId, rr.status AS reportStatus " +
            "FROM user_behavior b " +
            "LEFT JOIN wx_user u ON u.id = b.wx_user_id " +
            "LEFT JOIN animals a ON a.AnimalID = b.animal_id " +
            "LEFT JOIN adoption_request r ON r.id = b.request_id " +
            "LEFT JOIN rescue_report rr ON rr.id = b.report_id " +
            "<where>" +
            "<if test='event != null and event != \"\"'> AND b.event = #{event} </if>" +
            "</where>" +
            "ORDER BY b.id DESC" +
            "</script>")
    List<Map<String, Object>> selectAdminView(@Param("event") String event);
}
