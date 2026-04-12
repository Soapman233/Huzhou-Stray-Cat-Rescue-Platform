package com.hzsca.mapper;

import com.hzsca.entity.AdoptionRecord;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface AdoptionRecordMapper {
    @Insert("INSERT INTO adoption_record (request_id, animal_id, wx_user_id, contract_url, followup_plan) " +
            "VALUES (#{requestId}, #{animalId}, #{wxUserId}, #{contractUrl}, #{followupPlan})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(AdoptionRecord record);

    @Select("SELECT id, request_id AS requestId, animal_id AS animalId, wx_user_id AS wxUserId, adopt_time AS adoptTime, contract_url AS contractUrl, followup_plan AS followupPlan " +
            "FROM adoption_record WHERE request_id = #{requestId}")
    AdoptionRecord selectByRequestId(@Param("requestId") Long requestId);
}
