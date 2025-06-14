package com.hzsca.mapper;

import com.hzsca.entity.Activity;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface ActivityMapper {
    @Select("SELECT * FROM activity ORDER BY date DESC LIMIT #{limit}")
    List<Activity> findLatestActivities(int limit);

    @Select("SELECT * FROM activity ORDER BY date DESC")
    List<Activity> findAllActivities();

    @Select("SELECT * FROM activity WHERE status = 'ended' ORDER BY date DESC")
    List<Activity> findEndedActivities();

    @Insert("INSERT INTO activity (title, date, Location, imgurl, detail, status) " +
            "VALUES (#{title}, #{date}, #{Location}, #{imgurl}, #{detail}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertActivity(Activity activity);

    @Update("UPDATE activity SET title=#{title}, date=#{date}, Location=#{Location}, " +
            "imgurl=#{imgurl}, detail=#{detail}, status=#{status} WHERE id=#{id}")
    int updateActivity(Activity activity);

    @Delete("DELETE FROM activity WHERE id=#{id}")
    int deleteActivity(Integer id);
}