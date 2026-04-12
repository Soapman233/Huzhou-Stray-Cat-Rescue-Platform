package com.hzsca.mapper;

import com.hzsca.entity.RescueReport;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

@Mapper
public interface RescueReportMapper {
    @Insert("INSERT INTO rescue_report (wx_user_id, reporter_type, reporter_id, lat, lng, address, description, image_urls, urgency, status) " +
            "VALUES (#{wxUserId}, #{reporterType}, #{reporterId}, #{lat}, #{lng}, #{address}, #{description}, #{imageUrls}, #{urgency}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(RescueReport report);

    @Select("SELECT id, wx_user_id AS wxUserId, reporter_type AS reporterType, reporter_id AS reporterId, lat, lng, address, description, image_urls AS imageUrls, urgency, status, handler_id AS handlerId, handle_result AS handleResult, create_time AS createTime, update_time AS updateTime " +
            "FROM rescue_report WHERE reporter_type = #{reporterType} AND reporter_id = #{reporterId} ORDER BY id DESC")
    List<RescueReport> selectByReporter(@Param("reporterType") String reporterType, @Param("reporterId") Long reporterId);

    @Select("SELECT id, wx_user_id AS wxUserId, reporter_type AS reporterType, reporter_id AS reporterId, lat, lng, address, description, image_urls AS imageUrls, urgency, status, handler_id AS handlerId, handle_result AS handleResult, create_time AS createTime, update_time AS updateTime " +
            "FROM rescue_report WHERE id = #{id}")
    RescueReport selectById(@Param("id") Long id);

    @Select("<script>" +
            "SELECT r.id AS reportId, r.status, r.urgency, r.create_time AS createTime, r.update_time AS updateTime, " +
            "r.lat, r.lng, r.address, r.description, r.image_urls AS imageUrls, r.handle_result AS handleResult, r.handler_id AS handlerId, " +
            "r.reporter_type AS reporterType, r.reporter_id AS reporterId, " +
            "CASE WHEN r.reporter_type = 'WX' THEN u.nickname ELSE w.Name END AS wxNickname, " +
            "CASE WHEN r.reporter_type = 'WX' THEN u.avatar ELSE w.Avatar END AS wxAvatar " +
            "FROM rescue_report r " +
            "LEFT JOIN wx_user u ON u.id = r.reporter_id AND r.reporter_type = 'WX' " +
            "LEFT JOIN users w ON w.UserID = r.reporter_id AND r.reporter_type = 'WEB' " +
            "<where>" +
            "<if test='status != null and status != \"\"'> AND r.status = #{status} </if>" +
            "</where>" +
            "ORDER BY r.id DESC" +
            "</script>")
    List<Map<String, Object>> selectAdminView(@Param("status") String status);

    @Update("UPDATE rescue_report SET status = 'IN_PROGRESS', handler_id = #{handlerId}, update_time = NOW() " +
            "WHERE id = #{id} AND status = 'OPEN'")
    int assign(@Param("id") Long id, @Param("handlerId") Long handlerId);

    @Update("UPDATE rescue_report SET status = 'CLOSED', handle_result = #{handleResult}, update_time = NOW() " +
            "WHERE id = #{id} AND status IN ('OPEN','IN_PROGRESS')")
    int close(@Param("id") Long id, @Param("handleResult") String handleResult);
}
