package com.hzsca.mapper;

import com.hzsca.entity.Animals;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
@Repository
public interface AnimalsMapper {
    /**
     * 获取所有猫咪
     */
    List<Animals> getAllCats();

    /**
     * 根据ID获取猫咪
     */
    Animals getCatById(@Param("id") Integer id);

    /**
     * 获取可领养的猫咪
     */
    List<Animals> getAdoptableCats();

    /**
     * 更新猫咪信息
     */
    int updateCat(Animals cat);

    /**
     * 添加新猫咪
     */
    int insertCat(Animals cat);

    /**
     * 删除猫咪
     */
    int deleteCat(@Param("id") Integer id);

    int updateAnimalStatus(@Param("id") Integer id, @Param("status") String status);

    int updateAnimalStatusIfCurrent(@Param("id") Integer id, @Param("fromStatus") String fromStatus, @Param("toStatus") String toStatus);
}
