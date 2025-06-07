package com.hzsca.mapper;

import com.hzsca.entity.Cats;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CatsMapper {
    @Select("SELECT * FROM animals")
    List<Cats> getAllCats();

    @Select("SELECT * FROM animals WHERE animalId = #{id}")
    Cats getCatById(Integer id);
}