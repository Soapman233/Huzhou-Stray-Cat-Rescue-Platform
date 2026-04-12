package com.hzsca.mapper;

import com.hzsca.entity.VolunteerApplication;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface VolunteerApplicationMapper {
    int insert(VolunteerApplication app);

    VolunteerApplication selectLatestByApplicant(@Param("applicantType") String applicantType, @Param("applicantId") Long applicantId);

    VolunteerApplication selectById(@Param("id") Long id);

    List<VolunteerApplication> selectPage(@Param("status") String status, @Param("offset") int offset, @Param("limit") int limit);

    int count(@Param("status") String status);

    int updateStatus(@Param("id") Long id, @Param("status") String status, @Param("auditorId") Long auditorId, @Param("auditReason") String auditReason);
}
