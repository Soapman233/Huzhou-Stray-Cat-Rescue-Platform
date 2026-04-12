package com.hzsca.service;

import com.hzsca.entity.AdoptionForm;

public interface RiskScoringService {
    ScoreResult score(AdoptionForm form);

    class ScoreResult {
        public Integer score;
        public String riskLevel;
        public String reasons;
    }
}

