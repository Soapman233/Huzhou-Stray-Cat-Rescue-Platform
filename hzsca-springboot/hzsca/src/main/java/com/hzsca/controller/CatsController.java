package com.hzsca.controller;

import com.hzsca.entity.Animals;
import com.hzsca.service.CatsService;
import com.hzsca.service.FileService;
import com.hzsca.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "猫咪管理", description = "猫咪信息的增删改查接口")
@RestController
@RequestMapping("/api/cats")
public class CatsController {

    @Autowired
    private CatsService catsService;

    @Autowired
    private FileService fileService;

    @Operation(summary = "获取所有猫咪", description = "获取系统中所有猫咪的列表")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "获取成功"),
            @ApiResponse(responseCode = "500", description = "服务器错误")
    })
    @GetMapping
    public Result getAllCats() {
        try {
            List<Animals> cats = catsService.getAllCats();
            return Result.success(cats);
        } catch (Exception e) {
            return Result.error("500", "获取猫咪列表失败: " + e.getMessage());
        }
    }

    @Operation(summary = "获取猫咪详情", description = "根据ID获取猫咪详细信息")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "获取成功"),
            @ApiResponse(responseCode = "404", description = "猫咪不存在"),
            @ApiResponse(responseCode = "500", description = "服务器错误")
    })
    @GetMapping("/{id}")
    public Result getCatById(@Parameter(description = "猫咪ID") @PathVariable Integer id) {
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

    @Operation(summary = "添加猫咪", description = "添加新的猫咪信息")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "添加成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "500", description = "服务器错误")
    })
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

    @Operation(summary = "更新猫咪信息", description = "更新指定猫咪的信息")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "更新成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "404", description = "猫咪不存在"),
            @ApiResponse(responseCode = "500", description = "服务器错误")
    })
    @PutMapping("/{id}")
    public Result updateCat(
            @Parameter(description = "猫咪ID") @PathVariable Integer id,
            @RequestBody Animals cat) {
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

    @Operation(summary = "删除猫咪", description = "删除指定的猫咪信息")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "删除成功"),
            @ApiResponse(responseCode = "404", description = "猫咪不存在"),
            @ApiResponse(responseCode = "500", description = "服务器错误")
    })
    @DeleteMapping("/{id}")
    public Result deleteCat(@Parameter(description = "猫咪ID") @PathVariable Integer id) {
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

    @Operation(summary = "获取可领养猫咪", description = "获取所有可以被领养的猫咪列表")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "获取成功"),
            @ApiResponse(responseCode = "500", description = "服务器错误")
    })
    @GetMapping("/adoptable")
    public Result getAdoptableCats() {
        try {
            List<Animals> adoptableCats = catsService.getAdoptableCats();
            return Result.success(adoptableCats);
        } catch (Exception e) {
            return Result.error("500", "获取可领养猫咪列表失败: " + e.getMessage());
        }
    }

    @Operation(summary = "上传猫咪图片", description = "上传猫咪的照片")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "上传成功"),
            @ApiResponse(responseCode = "500", description = "上传失败")
    })
    @PostMapping("/upload")
    public Result uploadImage(@Parameter(description = "图片文件") @RequestParam("file") MultipartFile file) {
        try {
            String imageUrl = fileService.uploadFile(file);
            return Result.success(imageUrl);
        } catch (Exception e) {
            return Result.error("500", "上传图片失败: " + e.getMessage());
        }
    }
}