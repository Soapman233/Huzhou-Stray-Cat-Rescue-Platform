package com.hzsca.service.impl;

import com.hzsca.entity.Animals;
import com.hzsca.mapper.AnimalsMapper;
import com.hzsca.service.CatsService;
import com.hzsca.utils.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CatsServiceImpl implements CatsService {
    private static final Logger logger = LoggerFactory.getLogger(CatsServiceImpl.class);

    @Autowired
    private AnimalsMapper animalsMapper;

    @Autowired
    private RedisUtil redisUtil;

    private static final String CATS_CACHE_KEY = "cats:";
    private static final String ALL_CATS_KEY = CATS_CACHE_KEY + "all";
    private static final String ADOPTABLE_CATS_KEY = CATS_CACHE_KEY + "adoptable";
    private static final long CACHE_DURATION = 86400; // 24 h

    @Override
    public List<Animals> getAllCats() {
        // 尝试从缓存获取猫咪列表
        try {
            List<Animals> cats = (List<Animals>) redisUtil.get(ALL_CATS_KEY);
            if (cats != null) {
                logger.debug("Retrieved cats from cache");
                return cats;
            }
        } catch (Exception e) {
            logger.warn("Failed to get cats from cache: {}", e.getMessage());
        }

        //如果缓存中没有猫咪列表，则从数据库中获取
        List<Animals> cats = animalsMapper.getAllCats();

        //尝试存储猫咪列表到缓存
        if (cats != null && !cats.isEmpty()) {
            try {
                redisUtil.set(ALL_CATS_KEY, cats, CACHE_DURATION);
                logger.debug("Stored cats in cache");
            } catch (Exception e) {
                logger.warn("Failed to store cats in cache: {}", e.getMessage());
            }
        }

        return cats;
    }

    @Override
    public Animals getCatById(Integer id) {
        String cacheKey = CATS_CACHE_KEY + id;


        try {
            Animals cat = (Animals) redisUtil.get(cacheKey);
            if (cat != null) {
                logger.debug("Retrieved cat {} from cache", id);
                return cat;
            }
        } catch (Exception e) {
            logger.warn("Failed to get cat {} from cache: {}", id, e.getMessage());
        }


        Animals cat = animalsMapper.getCatById(id);


        if (cat != null) {
            try {
                redisUtil.set(cacheKey, cat, CACHE_DURATION);
                logger.debug("Stored cat {} in cache", id);
            } catch (Exception e) {
                logger.warn("Failed to store cat {} in cache: {}", id, e.getMessage());
            }
        }

        return cat;
    }

    @Override
    public List<Animals> getAdoptableCats() {

        try {
            List<Animals> cats = (List<Animals>) redisUtil.get(ADOPTABLE_CATS_KEY);
            if (cats != null) {
                logger.debug("Retrieved adoptable cats from cache");
                return cats;
            }
        } catch (Exception e) {
            logger.warn("Failed to get adoptable cats from cache: {}", e.getMessage());
        }


        List<Animals> cats = animalsMapper.getAdoptableCats();


        if (cats != null && !cats.isEmpty()) {
            try {
                redisUtil.set(ADOPTABLE_CATS_KEY, cats, CACHE_DURATION);
                logger.debug("Stored adoptable cats in cache");
            } catch (Exception e) {
                logger.warn("Failed to store adoptable cats in cache: {}", e.getMessage());
            }
        }

        return cats;
    }

    @Override
    @Transactional
    public boolean updateCat(Animals cat) {
        int result = animalsMapper.updateCat(cat);
        if (result > 0) {
            try {

                clearCatCaches(cat.getAnimalId());
                logger.debug("Cleared cache for cat {}", cat.getAnimalId());
            } catch (Exception e) {
                logger.warn("Failed to clear cache for cat {}: {}", cat.getAnimalId(), e.getMessage());
            }
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public boolean addCat(Animals cat) {
        int result = animalsMapper.insertCat(cat);
        if (result > 0) {
            try {
                // Try to clear list caches
                redisUtil.delete(ALL_CATS_KEY);
                redisUtil.delete(ADOPTABLE_CATS_KEY);
                logger.debug("Cleared list caches after adding new cat");
            } catch (Exception e) {
                logger.warn("Failed to clear list caches after adding new cat: {}", e.getMessage());
            }
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public boolean deleteCat(Integer id) {
        int result = animalsMapper.deleteCat(id);
        if (result > 0) {
            try {
                // Try to clear all related caches
                clearCatCaches(id);
                logger.debug("Cleared cache for deleted cat {}", id);
            } catch (Exception e) {
                logger.warn("Failed to clear cache for deleted cat {}: {}", id, e.getMessage());
            }
            return true;
        }
        return false;
    }

    @Override
    public void preloadCatsToCache() {
        try {
            // Load all cats
            List<Animals> allCats = animalsMapper.getAllCats();
            if (allCats != null && !allCats.isEmpty()) {
                redisUtil.set(ALL_CATS_KEY, allCats, CACHE_DURATION);
                logger.debug("Preloaded all cats to cache");

                // Cache individual cats
                for (Animals cat : allCats) {
                    String cacheKey = CATS_CACHE_KEY + cat.getAnimalId();
                    redisUtil.set(cacheKey, cat, CACHE_DURATION);
                }
                logger.debug("Preloaded individual cats to cache");
            }

            // Load adoptable cats
            List<Animals> adoptableCats = animalsMapper.getAdoptableCats();
            if (adoptableCats != null && !adoptableCats.isEmpty()) {
                redisUtil.set(ADOPTABLE_CATS_KEY, adoptableCats, CACHE_DURATION);
                logger.debug("Preloaded adoptable cats to cache");
            }
        } catch (Exception e) {
            logger.error("Failed to preload cats to cache: {}", e.getMessage());
        }
    }

    private void clearCatCaches(Integer catId) {

        redisUtil.delete(CATS_CACHE_KEY + catId);


        redisUtil.delete(ALL_CATS_KEY);
        redisUtil.delete(ADOPTABLE_CATS_KEY);
    }
}