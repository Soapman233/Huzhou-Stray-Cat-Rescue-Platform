package com.hzsca.service.impl;

import com.hzsca.entity.AdoptionApplication;
import com.hzsca.mapper.AdoptionApplicationMapper;
import com.hzsca.service.AdoptionApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AdoptionApplicationServiceImpl implements AdoptionApplicationService {
    @Autowired
    private AdoptionApplicationMapper adoptionApplicationMapper;

    @Override
    public int submitAssessment(AdoptionApplication application) {
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
    public int updateAssessment(AdoptionApplication application) {
        return adoptionApplicationMapper.update(application);
    }
}