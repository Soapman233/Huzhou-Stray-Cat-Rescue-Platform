package com.hzsca.mapper;

import com.hzsca.entity.AdoptionApplication;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import java.util.List;

@Mapper
public interface AdoptionApplicationMapper {
    @Insert("INSERT INTO adoption_assessment (AdopterID, Name, FamilyIncome, HasOwnedPets, " +
            "Smoker, Asthma, Allergy, LivingSpace, PetFriendly, Status) " +
            "VALUES (#{adopterId}, #{name}, #{familyIncome}, #{hasOwnedPets}, " +
            "#{smoker}, #{asthma}, #{allergy}, #{livingSpace}, #{petFriendly}, #{status})")
    int insert(AdoptionApplication application);

    @Select("SELECT * FROM adoption_assessment WHERE AssessmentID = #{assessmentId}")
    AdoptionApplication selectById(Integer assessmentId);

    @Select("SELECT * FROM adoption_assessment WHERE AdopterID = #{adopterId}")
    List<AdoptionApplication> selectByAdopterId(Integer adopterId);

    @Select("<script>" +
            "SELECT * FROM adoption_assessment " +
            "<where>" +
            "<if test='name != null and name != \"\"'>" +
            "AND Name LIKE CONCAT('%', #{name}, '%')" +
            "</if>" +
            "</where>" +
            "ORDER BY AssessmentID DESC" +
            "</script>")
    List<AdoptionApplication> selectAllWithName(String name);

    @Update("UPDATE adoption_assessment SET " +
            "FamilyIncome = #{familyIncome}, HasOwnedPets = #{hasOwnedPets}, " +
            "Smoker = #{smoker}, Asthma = #{asthma}, Allergy = #{allergy}, " +
            "LivingSpace = #{livingSpace}, PetFriendly = #{petFriendly}, " +
            "Status = #{status} " +
            "WHERE AssessmentID = #{assessmentId}")
    int update(AdoptionApplication application);
}
