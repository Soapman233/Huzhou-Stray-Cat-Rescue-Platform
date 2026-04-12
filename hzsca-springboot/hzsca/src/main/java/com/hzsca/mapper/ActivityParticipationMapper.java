package com.hzsca.mapper;

import com.hzsca.entity.ActivityParticipation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ActivityParticipationMapper {
    int insert(ActivityParticipation p);

    List<ActivityParticipation> selectByActivity(@Param("activityId") Integer activityId, @Param("status") String status);

    ActivityParticipation selectByUnique(@Param("activityId") Integer activityId, @Param("participantType") String participantType, @Param("participantId") Long participantId);

    int updateStatus(@Param("id") Long id, @Param("status") String status, @Param("auditorId") Long auditorId, @Param("auditReason") String auditReason);

    int countByActivity(@Param("activityId") Integer activityId);
}
