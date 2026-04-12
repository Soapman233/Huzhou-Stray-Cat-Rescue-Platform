package com.hzsca.mapper;

import com.hzsca.entity.AdoptionForm;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface AdoptionFormMapper {
    @Insert("INSERT INTO adoption_form (request_id, family_income, has_owned_pets, smoker, asthma, allergy, living_space, pet_friendly, work_hours, rent_or_own, family_members, child_in_home, accept_return_visit, remarks) " +
            "VALUES (#{requestId}, #{familyIncome}, #{hasOwnedPets}, #{smoker}, #{asthma}, #{allergy}, #{livingSpace}, #{petFriendly}, #{workHours}, #{rentOrOwn}, #{familyMembers}, #{childInHome}, #{acceptReturnVisit}, #{remarks})")
    int insert(AdoptionForm form);

    @Select("SELECT request_id AS requestId, family_income AS familyIncome, has_owned_pets AS hasOwnedPets, smoker, asthma, allergy, living_space AS livingSpace, pet_friendly AS petFriendly, work_hours AS workHours, rent_or_own AS rentOrOwn, family_members AS familyMembers, child_in_home AS childInHome, accept_return_visit AS acceptReturnVisit, remarks " +
            "FROM adoption_form WHERE request_id = #{requestId}")
    AdoptionForm selectByRequestId(@Param("requestId") Long requestId);
}
