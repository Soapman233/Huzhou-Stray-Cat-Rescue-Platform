package com.hzsca.mapper;

import com.hzsca.entity.Animals;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface CatsMapper {
    List<Animals> getAllCats();
    Animals getCatById(Integer id);
}