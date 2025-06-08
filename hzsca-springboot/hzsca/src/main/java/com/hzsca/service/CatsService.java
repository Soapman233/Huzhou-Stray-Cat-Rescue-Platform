package com.hzsca.service;

import com.hzsca.entity.Animals;
import com.hzsca.mapper.CatsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CatsService {
    @Autowired
    private CatsMapper catsMapper;

    public List<Animals> getAllCats() {
        return catsMapper.getAllCats();
    }

    public Animals getCatById(Integer id) {
        return catsMapper.getCatById(id);
    }
}