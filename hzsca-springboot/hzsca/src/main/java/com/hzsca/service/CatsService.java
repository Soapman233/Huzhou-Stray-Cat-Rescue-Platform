package com.hzsca.service;

import com.hzsca.entity.Animals;
import java.util.List;

public interface CatsService {
    List<Animals> getAllCats();

    Animals getCatById(Integer id);

    List<Animals> getAdoptableCats();

    boolean updateCat(Animals cat);

    boolean addCat(Animals cat);

    boolean deleteCat(Integer id);

    void preloadCatsToCache();
}