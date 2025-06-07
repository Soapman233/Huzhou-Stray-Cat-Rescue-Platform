package com.hzsca.service;

import com.hzsca.entity.Cats;
import com.hzsca.mapper.CatsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CatsService {
    @Autowired
    private CatsMapper catsMapper;

    public List<Cats> getAllCats() {
        return catsMapper.getAllCats();
    }

    public Cats getCatById(Integer id) {
        return catsMapper.getCatById(id);
    }
}