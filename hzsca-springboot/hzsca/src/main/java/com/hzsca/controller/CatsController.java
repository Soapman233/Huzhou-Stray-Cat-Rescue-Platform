package com.hzsca.controller;

import com.hzsca.entity.Animals;
import com.hzsca.service.CatsService;
import com.hzsca.service.FileService;
import com.hzsca.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/cats")
public class CatsController {

    @Autowired
    private CatsService catsService;

    @Autowired
    private FileService fileService;

    @GetMapping
    public Result getAllCats() {
        try {
            List<Animals> cats = catsService.getAllCats();
            return Result.success(cats);
        } catch (Exception e) {
            return Result.error("500", "获取猫咪列表失败: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public Result getCatById(@PathVariable Integer id) {
        try {
            Animals cat = catsService.getCatById(id);
            if (cat != null) {
                return Result.success(cat);
            }
            return Result.error("404", "未找到指定猫咪");
        } catch (Exception e) {
            return Result.error("500", "获取猫咪信息失败: " + e.getMessage());
        }
    }

    @PostMapping
    public Result addCat(@RequestBody Animals cat) {
        try {
            boolean success = catsService.addCat(cat);
            if (success) {
                return Result.success(cat);
            }
            return Result.error("500", "添加猫咪失败");
        } catch (IllegalArgumentException e) {
            return Result.error("400", e.getMessage());
        } catch (Exception e) {
            return Result.error("500", "添加猫咪失败: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public Result updateCat(@PathVariable Integer id, @RequestBody Animals cat) {
        try {
            cat.setAnimalId(id);
            boolean success = catsService.updateCat(cat);
            if (success) {
                return Result.success(cat);
            }
            return Result.error("404", "未找到指定猫咪");
        } catch (IllegalArgumentException e) {
            return Result.error("400", e.getMessage());
        } catch (Exception e) {
            return Result.error("500", "更新猫咪信息失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public Result deleteCat(@PathVariable Integer id) {
        try {
            boolean success = catsService.deleteCat(id);
            if (success) {
                return Result.success();
            }
            return Result.error("404", "未找到指定猫咪");
        } catch (Exception e) {
            return Result.error("500", "删除猫咪失败: " + e.getMessage());
        }
    }

    @GetMapping("/adoptable")
    public Result getAdoptableCats() {
        try {
            List<Animals> adoptableCats = catsService.getAdoptableCats();
            return Result.success(adoptableCats);
        } catch (Exception e) {
            return Result.error("500", "获取可领养猫咪列表失败: " + e.getMessage());
        }
    }

    @PostMapping("/upload")
    public Result uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            String imageUrl = fileService.uploadFile(file);
            return Result.success(imageUrl);
        } catch (Exception e) {
            return Result.error("500", "上传图片失败: " + e.getMessage());
        }
    }
}