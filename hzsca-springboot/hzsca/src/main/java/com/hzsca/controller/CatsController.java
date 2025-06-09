package com.hzsca.controller;

import com.hzsca.entity.Animals;
import com.hzsca.service.CatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CatsController {

    @Autowired
    private CatsService catsService;

    // 获取所有猫咪
    @GetMapping("/cats")
    public List<Animals> getAllCats() {
        return catsService.getAllCats();
    }

    // 根据ID获取单个猫咪
    @GetMapping("/cats/{id}")
    public Animals getCatById(@PathVariable Integer id) {
        return catsService.getCatById(id);
    }
}