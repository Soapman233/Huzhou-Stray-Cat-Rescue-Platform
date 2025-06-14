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

    @Insert("INSERT INTO activity (title, date, Location, status, detail, imgurl) " +
            "VALUES (#{title}, #{date}, #{Location}, #{status}, #{detail}, #{imgurl})")
    void insertActivity(Activity activity);

    @Update("UPDATE activity SET title=#{title}, date=#{date}, Location=#{Location}, " +
            "status=#{status}, detail=#{detail}, imgurl=#{imgurl} WHERE id=#{id}")
    void updateActivity(Activity activity);

    @Delete("DELETE FROM activity WHERE id=#{id}")
    void deleteActivity(Integer id);
}