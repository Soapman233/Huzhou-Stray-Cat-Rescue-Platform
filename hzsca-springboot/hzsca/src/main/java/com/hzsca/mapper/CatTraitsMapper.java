package com.hzsca.mapper;

import com.hzsca.entity.CatTraits;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CatTraitsMapper {
    CatTraits selectByCatId(@Param("catId") Integer catId);

    List<CatTraits> selectByCatIds(@Param("catIds") List<Integer> catIds);

    List<CatTraits> selectAll();

    int upsert(CatTraits traits);

    int deleteByCatId(@Param("catId") Integer catId);
}
