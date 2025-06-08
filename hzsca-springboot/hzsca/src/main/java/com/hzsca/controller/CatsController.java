package com.hzsca.controller;

import com.hzsca.common.Result;
import com.hzsca.entity.Animals;
import com.hzsca.service.CatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cats")
public class CatsController {
    @Autowired
    private CatsService catsService;

    @GetMapping
    public Result getAllCats() {
        try {
            List<Animals> animals = catsService.getAllCats();
            return Result.success(animals);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("500", "获取猫咪列表失败");
        }
    }

    @GetMapping("/{id}")
    public Result getCatById(@PathVariable Integer id) {
        try {
            Animals animals = catsService.getCatById(id);
            if (animals == null) {
                return Result.error("404", "未找到指定ID的猫咪");
            }
            return Result.success(animals);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("500", "获取猫咪详情失败");
        }
    }
}