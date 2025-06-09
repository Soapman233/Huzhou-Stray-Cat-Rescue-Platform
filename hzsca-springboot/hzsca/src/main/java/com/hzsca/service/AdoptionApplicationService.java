package com.hzsca.service;

import com.hzsca.entity.AdoptionApplication;
import java.util.List;

public interface AdoptionApplicationService {
    int submitAssessment(AdoptionApplication application);
    AdoptionApplication getAssessmentById(Integer assessmentId);
    List<AdoptionApplication> getAssessmentsByAdopterId(Integer adopterId);
    int updateAssessment(AdoptionApplication application);
}