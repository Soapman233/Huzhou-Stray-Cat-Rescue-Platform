package com.hzsca.mapper;

import com.hzsca.entity.Activity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface ActivityMapper {
    @Select("SELECT * FROM activity ORDER BY date DESC LIMIT #{limit}")
    List<Activity> findLatestActivities(int limit);

    @Select("SELECT * FROM activity ORDER BY date DESC")
    List<Activity> findAllActivities();

    @Select("SELECT * FROM activity WHERE status = 'ended' ORDER BY date DESC")
    List<Activity> findEndedActivities();
}