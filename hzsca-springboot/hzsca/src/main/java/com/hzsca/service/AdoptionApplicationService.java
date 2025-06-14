package com.hzsca.service;

import com.hzsca.entity.AdoptionApplication;
import java.util.List;
import java.util.Map;

public interface AdoptionApplicationService {
    int submitAssessment(AdoptionApplication application);
    AdoptionApplication getAssessmentById(Integer assessmentId);
    List<AdoptionApplication> getAssessmentsByAdopterId(Integer adopterId);
    int updateAssessment(AdoptionApplication application);
    Map<String, Object> getAllAssessments(Integer page, Integer size, String name);
    void approveAssessment(Integer assessmentId);
    void rejectAssessment(Integer assessmentId);
}