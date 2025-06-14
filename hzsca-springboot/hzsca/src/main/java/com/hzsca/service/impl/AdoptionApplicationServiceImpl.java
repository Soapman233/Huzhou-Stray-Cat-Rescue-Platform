package com.hzsca.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hzsca.entity.AdoptionApplication;
import com.hzsca.mapper.AdoptionApplicationMapper;
import com.hzsca.service.AdoptionApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AdoptionApplicationServiceImpl implements AdoptionApplicationService {

    @Autowired
    private AdoptionApplicationMapper adoptionApplicationMapper;

    @Override
    @Transactional
    public int submitAssessment(AdoptionApplication application) {
        // 设置初始状态为待审核
        application.setStatus("PENDING");
        return adoptionApplicationMapper.insert(application);
    }

    @Override
    public AdoptionApplication getAssessmentById(Integer assessmentId) {
        return adoptionApplicationMapper.selectById(assessmentId);
    }

    @Override
    public List<AdoptionApplication> getAssessmentsByAdopterId(Integer adopterId) {
        return adoptionApplicationMapper.selectByAdopterId(adopterId);
    }

    @Override
    public Map<String, Object> getAllAssessments(Integer page, Integer size, String name) {
        if (page == null || page < 1) page = 1;
        if (size == null || size < 1) size = 10;

        PageHelper.startPage(page, size);
        List<AdoptionApplication> applications = adoptionApplicationMapper.selectAllWithName(name);
        PageInfo<AdoptionApplication> pageInfo = new PageInfo<>(applications);

        Map<String, Object> result = new HashMap<>();
        result.put("records", pageInfo.getList());
        result.put("total", pageInfo.getTotal());
        result.put("pages", pageInfo.getPages());
        result.put("current", pageInfo.getPageNum());
        result.put("size", pageInfo.getPageSize());

        return result;
    }

    @Override
    @Transactional
    public int updateAssessment(AdoptionApplication application) {
        // 获取原有申请信息
        AdoptionApplication existingApplication = getAssessmentById(application.getAssessmentId());
        if (existingApplication == null) {
            throw new RuntimeException("申请不存在");
        }

        // 保持原有状态不变
        application.setStatus(existingApplication.getStatus());
        return adoptionApplicationMapper.update(application);
    }

    @Override
    @Transactional
    public void approveAssessment(Integer assessmentId) {
        AdoptionApplication application = getAssessmentById(assessmentId);
        if (application == null) {
            throw new RuntimeException("申请不存在");
        }

        // 允许处理待审核、已通过和已拒绝状态的申请
        if (!"PENDING".equals(application.getStatus()) &&
                !"APPROVED".equals(application.getStatus()) &&
                !"REJECTED".equals(application.getStatus())) {
            throw new RuntimeException("无效的申请状态");
        }

        // 更新申请状态为已通过
        application.setStatus("APPROVED");
        int rows = adoptionApplicationMapper.update(application);
        if (rows <= 0) {
            throw new RuntimeException("更新申请状态失败");
        }
    }

    @Override
    @Transactional
    public void rejectAssessment(Integer assessmentId) {
        AdoptionApplication application = getAssessmentById(assessmentId);
        if (application == null) {
            throw new RuntimeException("申请不存在");
        }

        if ("REJECTED".equals(application.getStatus())) {
            throw new RuntimeException("已拒绝的申请不能再次拒绝");
        }

        // 更新申请状态为已拒绝
        application.setStatus("REJECTED");
        int rows = adoptionApplicationMapper.update(application);
        if (rows <= 0) {
            throw new RuntimeException("更新申请状态失败");
        }
    }
}