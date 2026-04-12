package com.hzsca.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hzsca.common.Result;
import com.hzsca.mapper.RescueReportMapper;
import com.hzsca.security.SecurityUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/rescue-reports")
public class AdminRescueReportController {
    private final RescueReportMapper rescueReportMapper;

    public AdminRescueReportController(RescueReportMapper rescueReportMapper) {
        this.rescueReportMapper = rescueReportMapper;
    }

    @GetMapping
    public Result list(
            @RequestParam(required = false) String status,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size
    ) {
        if (page == null || page < 1) page = 1;
        if (size == null || size < 1) size = 10;
        PageHelper.startPage(page, size);
        List<Map<String, Object>> list = rescueReportMapper.selectAdminView(status);
        PageInfo<Map<String, Object>> pageInfo = new PageInfo<>(list);
        Map<String, Object> data = new HashMap<>();
        data.put("records", pageInfo.getList());
        data.put("total", pageInfo.getTotal());
        data.put("pages", pageInfo.getPages());
        data.put("current", pageInfo.getPageNum());
        data.put("size", pageInfo.getPageSize());
        return Result.success(data);
    }

    @PostMapping(value = "/{id}/assign", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Result assign(@PathVariable("id") Long id) {
        Long adminId = SecurityUtils.getCurrentUserId();
        if (adminId == null) {
            return Result.error("401", "未登录");
        }
        int rows = rescueReportMapper.assign(id, adminId);
        if (rows <= 0) {
            return Result.error("400", "当前状态不可指派");
        }
        return Result.success();
    }

    @PostMapping(value = "/{id}/close", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Result close(@PathVariable("id") Long id, @RequestBody(required = false) CloseRequest request) {
        String result = request == null ? null : request.handleResult;
        int rows = rescueReportMapper.close(id, result);
        if (rows <= 0) {
            return Result.error("400", "当前状态不可关闭");
        }
        return Result.success();
    }

    public static class CloseRequest {
        public String handleResult;
    }
}

