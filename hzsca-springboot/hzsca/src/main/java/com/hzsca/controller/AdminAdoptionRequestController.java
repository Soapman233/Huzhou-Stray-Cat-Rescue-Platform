package com.hzsca.controller;

import com.hzsca.common.Result;
import com.hzsca.security.SecurityUtils;
import com.hzsca.service.AdoptionRequestService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/adoption-requests")
public class AdminAdoptionRequestController {
    private final AdoptionRequestService adoptionRequestService;

    public AdminAdoptionRequestController(AdoptionRequestService adoptionRequestService) {
        this.adoptionRequestService = adoptionRequestService;
    }

    @GetMapping
    public Result list(
            @RequestParam(required = false) String status,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size
    ) {
        return adoptionRequestService.adminList(status, page, size);
    }

    @GetMapping("/{id}")
    public Result detail(@PathVariable("id") Long id) {
        return adoptionRequestService.adminDetail(id);
    }

    @PostMapping(value = "/{id}/approve", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Result approve(@PathVariable("id") Long id, @RequestBody(required = false) AuditRequest request) {
        Long adminId = SecurityUtils.getCurrentUserId();
        String reason = request == null ? null : request.reason;
        return adoptionRequestService.approve(adminId, id, reason);
    }

    @PostMapping(value = "/{id}/reject", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Result reject(@PathVariable("id") Long id, @RequestBody(required = false) AuditRequest request) {
        Long adminId = SecurityUtils.getCurrentUserId();
        String reason = request == null ? null : request.reason;
        return adoptionRequestService.reject(adminId, id, reason);
    }

    public static class AuditRequest {
        public String reason;
    }
}

