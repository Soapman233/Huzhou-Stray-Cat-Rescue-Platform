package com.hzsca.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hzsca.common.Result;
import com.hzsca.mapper.AdoptionRequestMapper;
import com.hzsca.mapper.UserBehaviorMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/dataset")
public class AdminDatasetController {
    private final UserBehaviorMapper userBehaviorMapper;
    private final AdoptionRequestMapper adoptionRequestMapper;

    public AdminDatasetController(UserBehaviorMapper userBehaviorMapper, AdoptionRequestMapper adoptionRequestMapper) {
        this.userBehaviorMapper = userBehaviorMapper;
        this.adoptionRequestMapper = adoptionRequestMapper;
    }

    @GetMapping("/behaviors")
    public Result behaviors(
            @RequestParam(required = false) String event,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "100") Integer size
    ) {
        if (page == null || page < 1) page = 1;
        if (size == null || size < 1) size = 100;
        PageHelper.startPage(page, size);
        List<Map<String, Object>> list = userBehaviorMapper.selectAdminView(event);
        PageInfo<Map<String, Object>> pageInfo = new PageInfo<>(list);
        Map<String, Object> data = new HashMap<>();
        data.put("records", pageInfo.getList());
        data.put("total", pageInfo.getTotal());
        data.put("pages", pageInfo.getPages());
        data.put("current", pageInfo.getPageNum());
        data.put("size", pageInfo.getPageSize());
        return Result.success(data);
    }

    @GetMapping("/adoptions")
    public Result adoptions(
            @RequestParam(required = false) String status,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "100") Integer size
    ) {
        if (page == null || page < 1) page = 1;
        if (size == null || size < 1) size = 100;
        PageHelper.startPage(page, size);
        List<Map<String, Object>> list = adoptionRequestMapper.selectDatasetAdoptions(status);
        PageInfo<Map<String, Object>> pageInfo = new PageInfo<>(list);
        Map<String, Object> data = new HashMap<>();
        data.put("records", pageInfo.getList());
        data.put("total", pageInfo.getTotal());
        data.put("pages", pageInfo.getPages());
        data.put("current", pageInfo.getPageNum());
        data.put("size", pageInfo.getPageSize());
        return Result.success(data);
    }
}

