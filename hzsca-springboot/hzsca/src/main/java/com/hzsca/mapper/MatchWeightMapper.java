package com.hzsca.mapper;

import com.hzsca.entity.MatchWeight;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MatchWeightMapper {
    List<MatchWeight> selectAll();
}
