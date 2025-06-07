package com.hzsca.controller;

import com.hzsca.entity.Cats;
import com.hzsca.service.CatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cats")
public class CatsController {
    @Autowired
    private CatsService catsService;

    @GetMapping
    public List<Cats> getAllCats() {
        return catsService.getAllCats();
    }

    @GetMapping("/{id}")
    public Cats getCatById(@PathVariable Integer id) {
        return catsService.getCatById(id);
    }
}