package com.hzsca.mapper;

import com.hzsca.entity.AdoptionRequest;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

@Mapper
public interface AdoptionRequestMapper {
    @Insert("INSERT INTO adoption_request (animal_id, wx_user_id, applicant_type, applicant_id, status, score, risk_level, risk_reasons) " +
            "VALUES (#{animalId}, #{wxUserId}, #{applicantType}, #{applicantId}, #{status}, #{score}, #{riskLevel}, #{riskReasons})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(AdoptionRequest request);

    @Select("SELECT id, animal_id AS animalId, wx_user_id AS wxUserId, applicant_type AS applicantType, applicant_id AS applicantId, status, apply_time AS applyTime, " +
            "audit_time AS auditTime, auditor_id AS auditorId, audit_reason AS auditReason, " +
            "score, risk_level AS riskLevel, risk_reasons AS riskReasons " +
            "FROM adoption_request WHERE id = #{id}")
    AdoptionRequest selectById(@Param("id") Long id);

    @Select("SELECT id, animal_id AS animalId, wx_user_id AS wxUserId, applicant_type AS applicantType, applicant_id AS applicantId, status, apply_time AS applyTime, " +
            "audit_time AS auditTime, auditor_id AS auditorId, audit_reason AS auditReason, " +
            "score, risk_level AS riskLevel, risk_reasons AS riskReasons " +
            "FROM adoption_request WHERE wx_user_id = #{wxUserId} ORDER BY id DESC")
    List<AdoptionRequest> selectByWxUserId(@Param("wxUserId") Long wxUserId);

    @Select("SELECT id, animal_id AS animalId, wx_user_id AS wxUserId, applicant_type AS applicantType, applicant_id AS applicantId, status, apply_time AS applyTime, " +
            "audit_time AS auditTime, auditor_id AS auditorId, audit_reason AS auditReason, " +
            "score, risk_level AS riskLevel, risk_reasons AS riskReasons " +
            "FROM adoption_request WHERE applicant_type = #{applicantType} AND applicant_id = #{applicantId} ORDER BY id DESC")
    List<AdoptionRequest> selectByApplicant(@Param("applicantType") String applicantType, @Param("applicantId") Long applicantId);

    @Select("SELECT COUNT(1) FROM adoption_request WHERE animal_id = #{animalId} AND status IN ('PENDING','APPROVED')")
    int countActiveByAnimalId(@Param("animalId") Integer animalId);

    @Update("UPDATE adoption_request SET status = #{status}, audit_time = NOW(), auditor_id = #{auditorId}, audit_reason = #{auditReason} WHERE id = #{id}")
    int audit(@Param("id") Long id, @Param("status") String status, @Param("auditorId") Long auditorId, @Param("auditReason") String auditReason);

    @Update("UPDATE adoption_request SET status = #{status} WHERE id = #{id} AND applicant_type = #{applicantType} AND applicant_id = #{applicantId}")
    int updateStatusForApplicant(@Param("id") Long id, @Param("applicantType") String applicantType, @Param("applicantId") Long applicantId, @Param("status") String status);

    @Update("UPDATE adoption_request SET status = 'REJECTED', audit_time = NOW(), auditor_id = #{auditorId}, audit_reason = #{auditReason} " +
            "WHERE animal_id = #{animalId} AND status = 'PENDING' AND id <> #{keepId}")
    int rejectOtherPending(@Param("animalId") Integer animalId, @Param("keepId") Long keepId, @Param("auditorId") Long auditorId, @Param("auditReason") String auditReason);

    @Select("<script>" +
            "SELECT r.id, r.animal_id AS animalId, r.wx_user_id AS wxUserId, r.applicant_type AS applicantType, r.applicant_id AS applicantId, r.status, r.apply_time AS applyTime, " +
            "r.audit_time AS auditTime, r.auditor_id AS auditorId, r.audit_reason AS auditReason, " +
            "r.score, r.risk_level AS riskLevel, r.risk_reasons AS riskReasons " +
            "FROM adoption_request r " +
            "<where>" +
            "<if test='status != null and status != \"\"'> AND r.status = #{status} </if>" +
            "</where>" +
            "ORDER BY r.id DESC" +
            "</script>")
    List<AdoptionRequest> selectAll(@Param("status") String status);

    @Select("<script>" +
            "SELECT r.id AS requestId, r.status, r.apply_time AS applyTime, r.audit_time AS auditTime, r.audit_reason AS auditReason, " +
            "r.score, r.risk_level AS riskLevel, " +
            "r.animal_id AS animalId, a.Name AS animalName, a.ImageURL AS animalImageUrl, a.Status AS animalStatus, " +
            "r.applicant_type AS applicantType, r.applicant_id AS applicantId, " +
            "CASE WHEN r.applicant_type = 'WX' THEN u.nickname ELSE w.Name END AS wxNickname, " +
            "CASE WHEN r.applicant_type = 'WX' THEN u.avatar ELSE w.Avatar END AS wxAvatar " +
            "FROM adoption_request r " +
            "LEFT JOIN animals a ON a.AnimalID = r.animal_id " +
            "LEFT JOIN wx_user u ON u.id = r.applicant_id AND r.applicant_type = 'WX' " +
            "LEFT JOIN users w ON w.UserID = r.applicant_id AND r.applicant_type = 'WEB' " +
            "<where>" +
            "<if test='status != null and status != \"\"'> AND r.status = #{status} </if>" +
            "</where>" +
            "ORDER BY r.id DESC" +
            "</script>")
    List<Map<String, Object>> selectAdminView(@Param("status") String status);

    @Select("SELECT r.id AS requestId, r.status, r.apply_time AS applyTime, r.audit_time AS auditTime, r.audit_reason AS auditReason, " +
            "r.score, r.risk_level AS riskLevel, r.risk_reasons AS riskReasons, " +
            "r.animal_id AS animalId, a.Name AS animalName, a.ImageURL AS animalImageUrl, a.Status AS animalStatus " +
            "FROM adoption_request r " +
            "LEFT JOIN animals a ON a.AnimalID = r.animal_id " +
            "WHERE r.applicant_type = #{applicantType} AND r.applicant_id = #{applicantId} ORDER BY r.id DESC")
    List<Map<String, Object>> selectMyView(@Param("applicantType") String applicantType, @Param("applicantId") Long applicantId);

    @Select("<script>" +
            "SELECT " +
            "r.id AS requestId, r.status AS requestStatus, r.apply_time AS applyTime, r.audit_time AS auditTime, r.audit_reason AS auditReason, " +
            "r.score, r.risk_level AS riskLevel, r.risk_reasons AS riskReasons, " +
            "r.animal_id AS animalId, a.Name AS animalName, a.Breed AS animalBreed, a.Age AS animalAge, a.Gender AS animalGender, a.Source AS animalSource, a.Status AS animalStatus, " +
            "r.applicant_type AS applicantType, r.applicant_id AS applicantId, " +
            "CASE WHEN r.applicant_type = 'WX' THEN u.nickname ELSE w.Name END AS wxNickname, " +
            "CASE WHEN r.applicant_type = 'WX' THEN u.gender ELSE w.Gender END AS wxGender, " +
            "CASE WHEN r.applicant_type = 'WX' THEN u.city ELSE NULL END AS wxCity, " +
            "f.family_income AS familyIncome, f.has_owned_pets AS hasOwnedPets, f.smoker AS smoker, f.asthma AS asthma, f.allergy AS allergy, " +
            "f.living_space AS livingSpace, f.pet_friendly AS petFriendly, f.work_hours AS workHours, f.rent_or_own AS rentOrOwn, " +
            "f.family_members AS familyMembers, f.child_in_home AS childInHome, f.accept_return_visit AS acceptReturnVisit " +
            "FROM adoption_request r " +
            "LEFT JOIN adoption_form f ON f.request_id = r.id " +
            "LEFT JOIN animals a ON a.AnimalID = r.animal_id " +
            "LEFT JOIN wx_user u ON u.id = r.applicant_id AND r.applicant_type = 'WX' " +
            "LEFT JOIN users w ON w.UserID = r.applicant_id AND r.applicant_type = 'WEB' " +
            "<where>" +
            "<if test='status != null and status != \"\"'> AND r.status = #{status} </if>" +
            "</where>" +
            "ORDER BY r.id DESC" +
            "</script>")
    List<Map<String, Object>> selectDatasetAdoptions(@Param("status") String status);
}
