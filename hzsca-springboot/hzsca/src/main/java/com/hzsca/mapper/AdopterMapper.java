package com.hzsca.mapper;

import com.hzsca.entity.Adopter;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface AdopterMapper {
    @Insert("INSERT INTO Adopters (UserID, Name, Age, IDCardNumber, Phone, Email, Address, BirthDate) " +
            "VALUES (#{userId}, #{name}, #{age}, #{idCardNumber}, #{phone}, #{email}, #{address}, #{birthDate})")
    @Options(useGeneratedKeys = true, keyProperty = "adopterId", keyColumn = "AdopterID")
    int insert(Adopter adopter);

    @Select("SELECT * FROM Adopters WHERE AdopterID = #{adopterId}")
    Adopter selectById(Integer adopterId);

    @Select("SELECT * FROM Adopters WHERE UserID = #{userId}")
    Adopter selectByUserId(Integer userId);

    @Select("SELECT * FROM Adopters WHERE IDCardNumber = #{idCardNumber}")
    Adopter selectByIdCardNumber(String idCardNumber);
}