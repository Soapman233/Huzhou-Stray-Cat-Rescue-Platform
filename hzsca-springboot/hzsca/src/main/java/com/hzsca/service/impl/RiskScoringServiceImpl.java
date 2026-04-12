package com.hzsca.service.impl;

import com.hzsca.entity.AdoptionForm;
import com.hzsca.service.RiskScoringService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class RiskScoringServiceImpl implements RiskScoringService {
    @Override
    public ScoreResult score(AdoptionForm form) {
        ScoreResult result = new ScoreResult();
        int score = 100;
        List<String> reasons = new ArrayList<>();

        if (form == null) {
            result.score = 60;
            result.riskLevel = "MEDIUM";
            result.reasons = "未填写申请表，风险中等";
            return result;
        }

        if (Boolean.TRUE.equals(form.getAllergy())) {
            score -= 25;
            reasons.add("过敏=是(-25)");
        }
        if (Boolean.TRUE.equals(form.getAsthma())) {
            score -= 15;
            reasons.add("哮喘=是(-15)");
        }
        if (Boolean.TRUE.equals(form.getSmoker())) {
            score -= 10;
            reasons.add("吸烟=是(-10)");
        }

        Integer workHours = form.getWorkHours();
        if (workHours != null) {
            if (workHours >= 12) {
                score -= 12;
                reasons.add("工作时长>=12h(-12)");
            } else if (workHours >= 10) {
                score -= 8;
                reasons.add("工作时长>=10h(-8)");
            } else if (workHours <= 6) {
                score += 3;
                reasons.add("工作时长<=6h(+3)");
            }
        }

        BigDecimal income = form.getFamilyIncome();
        if (income != null) {
            if (income.compareTo(new BigDecimal("3000")) < 0) {
                score -= 10;
                reasons.add("家庭收入<3000(-10)");
            } else if (income.compareTo(new BigDecimal("8000")) >= 0) {
                score += 5;
                reasons.add("家庭收入>=8000(+5)");
            }
        }

        if (Boolean.FALSE.equals(form.getHasOwnedPets())) {
            score -= 5;
            reasons.add("养宠经验=否(-5)");
        } else if (Boolean.TRUE.equals(form.getHasOwnedPets())) {
            score += 2;
            reasons.add("养宠经验=是(+2)");
        }

        if (Boolean.TRUE.equals(form.getAcceptReturnVisit())) {
            score += 5;
            reasons.add("接受回访=是(+5)");
        } else if (Boolean.FALSE.equals(form.getAcceptReturnVisit())) {
            score -= 8;
            reasons.add("接受回访=否(-8)");
        }

        if (score < 0) score = 0;
        if (score > 100) score = 100;

        result.score = score;
        if (score >= 80) {
            result.riskLevel = "LOW";
        } else if (score >= 60) {
            result.riskLevel = "MEDIUM";
        } else {
            result.riskLevel = "HIGH";
        }

        result.reasons = reasons.isEmpty() ? "无明显风险项" : String.join("；", reasons);
        return result;
    }
}

