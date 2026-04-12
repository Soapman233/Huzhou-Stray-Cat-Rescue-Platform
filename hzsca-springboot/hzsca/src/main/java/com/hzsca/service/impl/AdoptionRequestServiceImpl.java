package com.hzsca.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hzsca.common.Result;
import com.hzsca.entity.AdoptionForm;
import com.hzsca.entity.AdoptionRecord;
import com.hzsca.entity.AdoptionRequest;
import com.hzsca.entity.Animals;
import com.hzsca.entity.UserBehavior;
import com.hzsca.entity.Users;
import com.hzsca.entity.WxUser;
import com.hzsca.mapper.AdoptionFormMapper;
import com.hzsca.mapper.AdoptionRecordMapper;
import com.hzsca.mapper.AdoptionRequestMapper;
import com.hzsca.mapper.AnimalsMapper;
import com.hzsca.mapper.UserMapper;
import com.hzsca.mapper.UserBehaviorMapper;
import com.hzsca.mapper.WxUserMapper;
import com.hzsca.service.AdoptionRequestService;
import com.hzsca.service.RiskScoringService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AdoptionRequestServiceImpl implements AdoptionRequestService {
    private final AdoptionRequestMapper adoptionRequestMapper;
    private final AdoptionFormMapper adoptionFormMapper;
    private final AdoptionRecordMapper adoptionRecordMapper;
    private final AnimalsMapper animalsMapper;
    private final WxUserMapper wxUserMapper;
    private final UserMapper userMapper;
    private final RiskScoringService riskScoringService;
    private final UserBehaviorMapper userBehaviorMapper;

    public AdoptionRequestServiceImpl(
            AdoptionRequestMapper adoptionRequestMapper,
            AdoptionFormMapper adoptionFormMapper,
            AdoptionRecordMapper adoptionRecordMapper,
            AnimalsMapper animalsMapper,
            WxUserMapper wxUserMapper,
            UserMapper userMapper,
            RiskScoringService riskScoringService,
            UserBehaviorMapper userBehaviorMapper
    ) {
        this.adoptionRequestMapper = adoptionRequestMapper;
        this.adoptionFormMapper = adoptionFormMapper;
        this.adoptionRecordMapper = adoptionRecordMapper;
        this.animalsMapper = animalsMapper;
        this.wxUserMapper = wxUserMapper;
        this.userMapper = userMapper;
        this.riskScoringService = riskScoringService;
        this.userBehaviorMapper = userBehaviorMapper;
    }

    @Override
    @Transactional
    public Result submit(Long applicantId, String applicantType, Integer animalId, AdoptionForm form) {
        if (applicantId == null || applicantType == null) {
            return Result.error("401", "未登录");
        }
        if (animalId == null) {
            return Result.error("400", "缺少 animalId");
        }
        Animals cat = animalsMapper.getCatById(animalId);
        if (cat == null) {
            return Result.error("404", "未找到指定猫咪");
        }
        if (cat.getStatus() != null && !"ADOPTABLE".equals(cat.getStatus())) {
            return Result.error("400", "该猫咪当前不可申请领养");
        }
        int locked = animalsMapper.updateAnimalStatusIfCurrent(animalId, "ADOPTABLE", "PENDING");
        if (locked <= 0) {
            return Result.error("400", "该猫咪已有进行中的申请");
        }

        AdoptionRequest request = new AdoptionRequest();
        request.setAnimalId(animalId);
        String type = normalizeApplicantType(applicantType);
        request.setApplicantType(type);
        request.setApplicantId(applicantId);
        request.setWxUserId("WX".equals(type) ? applicantId : null);
        request.setStatus("PENDING");
        RiskScoringService.ScoreResult scoreResult = riskScoringService.score(form);
        request.setScore(scoreResult.score);
        request.setRiskLevel(scoreResult.riskLevel);
        request.setRiskReasons(scoreResult.reasons);
        adoptionRequestMapper.insert(request);
        if (request.getId() == null) {
            return Result.error("500", "提交失败");
        }

        if (form != null) {
            form.setRequestId(request.getId());
            adoptionFormMapper.insert(form);
        }

        if ("WX".equals(type) && request.getWxUserId() != null) {
            UserBehavior behavior = new UserBehavior();
            behavior.setWxUserId(request.getWxUserId());
            behavior.setEvent("apply");
            behavior.setAnimalId(animalId);
            behavior.setRequestId(request.getId());
            behavior.setExtra("score=" + request.getScore() + ",riskLevel=" + request.getRiskLevel());
            userBehaviorMapper.insert(behavior);
        }

        Map<String, Object> data = new HashMap<>();
        data.put("requestId", request.getId());
        return Result.success(data);
    }

    @Override
    public Result myRequests(Long applicantId, String applicantType) {
        if (applicantId == null || applicantType == null) {
            return Result.error("401", "未登录");
        }
        List<Map<String, Object>> list = adoptionRequestMapper.selectMyView(normalizeApplicantType(applicantType), applicantId);
        return Result.success(list);
    }

    @Override
    public Result myRequestDetail(Long applicantId, String applicantType, Long requestId) {
        if (applicantId == null || applicantType == null) {
            return Result.error("401", "未登录");
        }
        if (requestId == null) {
            return Result.error("400", "缺少 requestId");
        }
        AdoptionRequest request = adoptionRequestMapper.selectById(requestId);
        String type = normalizeApplicantType(applicantType);
        if (request == null || request.getApplicantId() == null || request.getApplicantType() == null
                || !type.equalsIgnoreCase(request.getApplicantType()) || !request.getApplicantId().equals(applicantId)) {
            return Result.error("404", "未找到申请记录");
        }
        AdoptionForm form = adoptionFormMapper.selectByRequestId(requestId);
        Animals cat = request.getAnimalId() == null ? null : animalsMapper.getCatById(request.getAnimalId());
        AdoptionRecord record = adoptionRecordMapper.selectByRequestId(requestId);

        Map<String, Object> data = new HashMap<>();
        data.put("request", request);
        data.put("form", form);
        data.put("cat", cat);
        data.put("record", record);
        return Result.success(data);
    }

    @Override
    @Transactional
    public Result cancel(Long applicantId, String applicantType, Long requestId) {
        if (applicantId == null || applicantType == null) {
            return Result.error("401", "未登录");
        }
        if (requestId == null) {
            return Result.error("400", "缺少 requestId");
        }
        AdoptionRequest request = adoptionRequestMapper.selectById(requestId);
        String type = normalizeApplicantType(applicantType);
        if (request == null || request.getApplicantId() == null || request.getApplicantType() == null
                || !type.equalsIgnoreCase(request.getApplicantType()) || !request.getApplicantId().equals(applicantId)) {
            return Result.error("404", "未找到申请记录");
        }
        if (!"PENDING".equals(request.getStatus())) {
            return Result.error("400", "当前状态不可取消");
        }
        int rows = adoptionRequestMapper.updateStatusForApplicant(requestId, type, applicantId, "CANCELED");
        if (rows <= 0) {
            return Result.error("500", "取消失败");
        }
        Integer animalId = request.getAnimalId();
        if (animalId != null) {
            int active = adoptionRequestMapper.countActiveByAnimalId(animalId);
            if (active == 0) {
                animalsMapper.updateAnimalStatus(animalId, "ADOPTABLE");
            }
        }
        return Result.success();
    }

    @Override
    public Result adminList(String status, Integer page, Integer size) {
        if (page == null || page < 1) page = 1;
        if (size == null || size < 1) size = 10;
        PageHelper.startPage(page, size);
        List<Map<String, Object>> list = adoptionRequestMapper.selectAdminView(status);
        PageInfo<Map<String, Object>> pageInfo = new PageInfo<>(list);
        Map<String, Object> data = new HashMap<>();
        data.put("records", pageInfo.getList());
        data.put("total", pageInfo.getTotal());
        data.put("pages", pageInfo.getPages());
        data.put("current", pageInfo.getPageNum());
        data.put("size", pageInfo.getPageSize());
        return Result.success(data);
    }

    @Override
    public Result adminDetail(Long requestId) {
        if (requestId == null) {
            return Result.error("400", "缺少 requestId");
        }
        AdoptionRequest request = adoptionRequestMapper.selectById(requestId);
        if (request == null) {
            return Result.error("404", "未找到申请记录");
        }
        AdoptionForm form = adoptionFormMapper.selectByRequestId(requestId);
        Animals cat = request.getAnimalId() == null ? null : animalsMapper.getCatById(request.getAnimalId());
        WxUser wxUser = null;
        Users webUser = null;
        if ("WX".equalsIgnoreCase(request.getApplicantType())) {
            wxUser = request.getApplicantId() == null ? null : wxUserMapper.selectById(request.getApplicantId());
        } else if ("WEB".equalsIgnoreCase(request.getApplicantType())) {
            webUser = request.getApplicantId() == null ? null : userMapper.selectById(request.getApplicantId().intValue());
            if (webUser != null) {
                webUser.setPassword(null);
            }
        }
        AdoptionRecord record = adoptionRecordMapper.selectByRequestId(requestId);

        Map<String, Object> data = new HashMap<>();
        data.put("request", request);
        data.put("form", form);
        data.put("cat", cat);
        data.put("record", record);
        data.put("wxUser", wxUser);
        data.put("webUser", webUser);
        return Result.success(data);
    }

    @Override
    @Transactional
    public Result approve(Long adminId, Long requestId, String reason) {
        if (adminId == null) {
            return Result.error("401", "未登录");
        }
        AdoptionRequest request = adoptionRequestMapper.selectById(requestId);
        if (request == null) {
            return Result.error("404", "未找到申请记录");
        }
        if (!"PENDING".equals(request.getStatus())) {
            return Result.error("400", "当前状态不可通过");
        }
        adoptionRequestMapper.audit(requestId, "APPROVED", adminId, reason);
        Integer animalId = request.getAnimalId();
        if (animalId != null) {
            animalsMapper.updateAnimalStatus(animalId, "ADOPTED");
            adoptionRequestMapper.rejectOtherPending(animalId, requestId, adminId, "该猫咪已被领养");
        }
        if (adoptionRecordMapper.selectByRequestId(requestId) == null) {
            if ("WX".equalsIgnoreCase(request.getApplicantType()) && request.getWxUserId() != null) {
                AdoptionRecord record = new AdoptionRecord();
                record.setRequestId(requestId);
                record.setAnimalId(request.getAnimalId());
                record.setWxUserId(request.getWxUserId());
                adoptionRecordMapper.insert(record);
            }
        }
        return Result.success();
    }

    @Override
    @Transactional
    public Result reject(Long adminId, Long requestId, String reason) {
        if (adminId == null) {
            return Result.error("401", "未登录");
        }
        AdoptionRequest request = adoptionRequestMapper.selectById(requestId);
        if (request == null) {
            return Result.error("404", "未找到申请记录");
        }
        if (!"PENDING".equals(request.getStatus())) {
            return Result.error("400", "当前状态不可拒绝");
        }
        adoptionRequestMapper.audit(requestId, "REJECTED", adminId, reason);
        Integer animalId = request.getAnimalId();
        if (animalId != null) {
            int active = adoptionRequestMapper.countActiveByAnimalId(animalId);
            if (active == 0) {
                animalsMapper.updateAnimalStatus(animalId, "ADOPTABLE");
            }
        }
        return Result.success();
    }

    private String normalizeApplicantType(String type) {
        String t = type == null ? "" : type.trim();
        if ("wx".equalsIgnoreCase(t) || "WX".equalsIgnoreCase(t)) {
            return "WX";
        }
        if ("web".equalsIgnoreCase(t) || "WEB".equalsIgnoreCase(t)) {
            return "WEB";
        }
        if (t.isBlank() || "unknown".equalsIgnoreCase(t)) {
            return "WX";
        }
        return t.toUpperCase();
    }
}

