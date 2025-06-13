package com.hzsca.service;

import java.util.List;

public interface ActivityService {
    
    // 获取所有活动
    List<Object> getAllActivities();
    
    // 根据ID获取活动
    Object getActivityById(Long id);
    
    // 创建活动
    Object createActivity(Object activity);
    
    // 更新活动
    Object updateActivity(Long id, Object activity);
    
    // 删除活动
    void deleteActivity(Long id);
    
    // 获取已结束的活动
    List<Object> getEndedActivities();
}
