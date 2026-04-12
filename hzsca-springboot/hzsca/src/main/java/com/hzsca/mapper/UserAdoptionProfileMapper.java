package com.hzsca.mapper;

import com.hzsca.entity.UserAdoptionProfile;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserAdoptionProfileMapper {
    UserAdoptionProfile selectByUserId(@Param("userId") Long userId);

    int upsert(UserAdoptionProfile profile);

    int deleteByUserId(@Param("userId") Long userId);
}
