package com.hzsca.mapper;

import com.hzsca.entity.AdoptionApplication;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import java.util.List;

@Mapper
public interface AdoptionApplicationMapper {
    @Insert("INSERT INTO AdoptionAssessment (AdopterID, Name, FamilyIncome, HasOwnedPets, " +
            "Smoker, Asthma, Allergy, LivingSpace, PetFriendly) " +
            "VALUES (#{adopterId}, #{name}, #{familyIncome}, #{hasOwnedPets}, " +
            "#{smoker}, #{asthma}, #{allergy}, #{livingSpace}, #{petFriendly})")
    int insert(AdoptionApplication application);

    @Select("SELECT * FROM AdoptionAssessment WHERE AssessmentID = #{assessmentId}")
    AdoptionApplication selectById(Integer assessmentId);

    @Select("SELECT * FROM AdoptionAssessment WHERE AdopterID = #{adopterId}")
    List<AdoptionApplication> selectByAdopterId(Integer adopterId);

    @Update("UPDATE AdoptionAssessment SET " +
            "FamilyIncome = #{familyIncome}, HasOwnedPets = #{hasOwnedPets}, " +
            "Smoker = #{smoker}, Asthma = #{asthma}, Allergy = #{allergy}, " +
            "LivingSpace = #{livingSpace}, PetFriendly = #{petFriendly} " +
            "WHERE AssessmentID = #{assessmentId}")
    int update(AdoptionApplication application);
}