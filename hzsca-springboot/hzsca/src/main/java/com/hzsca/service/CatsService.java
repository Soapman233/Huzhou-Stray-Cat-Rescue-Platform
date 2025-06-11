package com.hzsca.service;

import com.hzsca.entity.Animals;
import java.util.List;

public interface CatsService {
    /**
     * 获取所有猫咪信息
     * @return 猫咪列表
     */
    List<Animals> getAllCats();

    /**
     * 根据ID获取猫咪信息
     * @param id 猫咪ID
     * @return 猫咪信息
     */
    Animals getCatById(Integer id);

    /**
     * 获取可领养的猫咪列表
     * @return 可领养的猫咪列表
     */
    List<Animals> getAdoptableCats();

    /**
     * 添加新猫咪
     * @param cat 猫咪信息
     * @return 是否添加成功
     */
    boolean addCat(Animals cat);

    /**
     * 更新猫咪信息
     * @param cat 猫咪信息
     * @return 是否更新成功
     */
    boolean updateCat(Animals cat);

    /**
     * 删除猫咪信息
     * @param id 猫咪ID
     * @return 是否删除成功
     */
    boolean deleteCat(Integer id);

    /**
     * 预加载猫咪信息到缓存
     */
    void preloadCatsToCache();
}