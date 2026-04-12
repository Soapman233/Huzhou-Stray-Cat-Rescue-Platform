package com.hzsca.service.impl;

import com.hzsca.entity.Animals;
import com.hzsca.entity.CatTraits;
import com.hzsca.entity.MatchWeight;
import com.hzsca.entity.UserAdoptionProfile;
import com.hzsca.mapper.AnimalsMapper;
import com.hzsca.mapper.CatTraitsMapper;
import com.hzsca.mapper.MatchWeightMapper;
import com.hzsca.mapper.UserAdoptionProfileMapper;
import com.hzsca.service.MatchService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class MatchServiceImpl implements MatchService {
    private final UserAdoptionProfileMapper profileMapper;
    private final AnimalsMapper animalsMapper;
    private final CatTraitsMapper catTraitsMapper;
    private final MatchWeightMapper matchWeightMapper;

    public MatchServiceImpl(
            UserAdoptionProfileMapper profileMapper,
            AnimalsMapper animalsMapper,
            CatTraitsMapper catTraitsMapper,
            MatchWeightMapper matchWeightMapper
    ) {
        this.profileMapper = profileMapper;
        this.animalsMapper = animalsMapper;
        this.catTraitsMapper = catTraitsMapper;
        this.matchWeightMapper = matchWeightMapper;
    }

    @Override
    public List<Recommendation> recommend(Long userId, int limit) {
        if (userId == null) {
            return List.of();
        }

        UserAdoptionProfile profile = profileMapper.selectByUserId(userId);
        if (profile == null) {
            return List.of();
        }

        List<Animals> cats = animalsMapper.getAdoptableCats();
        if (cats == null || cats.isEmpty()) {
            return List.of();
        }

        List<Integer> catIds = cats.stream()
                .map(Animals::getAnimalId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        Map<Integer, CatTraits> traitsMap = new HashMap<>();
        if (!catIds.isEmpty()) {
            List<CatTraits> traitsList = catTraitsMapper.selectByCatIds(catIds);
            if (traitsList != null) {
                for (CatTraits t : traitsList) {
                    if (t != null && t.getCatId() != null) {
                        traitsMap.put(t.getCatId(), t);
                    }
                }
            }
        }

        Map<String, Double> weights = loadWeights();

        List<Scored> scoredList = new ArrayList<>();
        for (Animals cat : cats) {
            if (cat == null || cat.getAnimalId() == null) {
                continue;
            }
            CatTraits traits = traitsMap.getOrDefault(cat.getAnimalId(), defaultTraits(cat.getAnimalId()));
            ScoreResult sr = score(profile, traits, weights);
            if (sr.rejected) {
                continue;
            }
            scoredList.add(new Scored(cat, sr.score, sr.reasons));
        }

        scoredList.sort(Comparator.comparing(Scored::score).reversed());
        int take = limit <= 0 ? 10 : Math.min(limit, 50);
        return scoredList.stream()
                .limit(take)
                .map(s -> new Recommendation(s.cat, s.score, s.reasons))
                .collect(Collectors.toList());
    }

    private Map<String, Double> loadWeights() {
        Map<String, Double> weights = defaultWeights();
        List<MatchWeight> list = matchWeightMapper.selectAll();
        if (list == null || list.isEmpty()) {
            return weights;
        }
        for (MatchWeight w : list) {
            if (w == null || w.getWeightKey() == null || w.getWeightKey().isBlank() || w.getWeightValue() == null) {
                continue;
            }
            weights.put(w.getWeightKey(), w.getWeightValue());
        }
        return weights;
    }

    private Map<String, Double> defaultWeights() {
        Map<String, Double> w = new HashMap<>();
        w.put("activity", 1.0);
        w.put("affection", 0.6);
        w.put("shedding", 1.2);
        w.put("space", 1.0);
        w.put("beginner", 1.0);
        w.put("personality", 1.0);
        return w;
    }

    private ScoreResult score(UserAdoptionProfile p, CatTraits t, Map<String, Double> weights) {
        List<String> reasons = new ArrayList<>();

        boolean allergy = Boolean.TRUE.equals(p.getAllergy());
        int maxShedding = safeInt(p.getMaxSheddingLevel(), 5);
        int shedding = safeInt(t.getSheddingLevel(), 3);
        if (allergy && shedding > maxShedding) {
            return ScoreResult.rejected("掉毛等级不符合过敏要求");
        }

        if (Boolean.TRUE.equals(p.getHasChildren()) && Boolean.FALSE.equals(t.getChildFriendly())) {
            return ScoreResult.rejected("家中有孩子但猫咪不适合与孩子共处");
        }

        if (Boolean.TRUE.equals(p.getHasOtherPets()) && Boolean.FALSE.equals(t.getPetFriendly())) {
            return ScoreResult.rejected("家中有其他宠物但猫咪不适合与其他宠物共处");
        }

        if (Boolean.FALSE.equals(p.getAcceptSpecialCare()) && Boolean.TRUE.equals(t.getSpecialCareNeeded())) {
            return ScoreResult.rejected("猫咪需要特殊照护但用户不接受");
        }

        int homeSize = safeInt(p.getHomeSizeLevel(), 2);
        int spaceNeed = safeInt(t.getSpaceNeedLevel(), 2);
        if (homeSize < spaceNeed) {
            return ScoreResult.rejected("居住空间可能不足以满足猫咪需求");
        }

        double totalWeight = 0.0;
        double weighted = 0.0;

        double activityW = weights.getOrDefault("activity", 1.0);
        double activityMatch = matchActivity(p, t);
        totalWeight += activityW;
        weighted += activityW * activityMatch;
        addReason(reasons, "活动量匹配", activityMatch);

        double affectionW = weights.getOrDefault("affection", 0.6);
        double affectionMatch = matchAffection(p, t);
        totalWeight += affectionW;
        weighted += affectionW * affectionMatch;
        addReason(reasons, "性格亲人度匹配", affectionMatch);

        double sheddingW = weights.getOrDefault("shedding", 1.2);
        double sheddingMatch = matchShedding(p, t);
        totalWeight += sheddingW;
        weighted += sheddingW * sheddingMatch;
        addReason(reasons, "掉毛容忍度匹配", sheddingMatch);

        double spaceW = weights.getOrDefault("space", 1.0);
        double spaceMatch = matchSpace(p, t);
        totalWeight += spaceW;
        weighted += spaceW * spaceMatch;
        addReason(reasons, "空间需求匹配", spaceMatch);

        double beginnerW = weights.getOrDefault("beginner", 1.0);
        double beginnerMatch = matchBeginner(p, t);
        totalWeight += beginnerW;
        weighted += beginnerW * beginnerMatch;
        addReason(reasons, "新手友好度匹配", beginnerMatch);

        double personalityW = weights.getOrDefault("personality", 1.0);
        double personalityMatch = matchSchedule(p, t);
        totalWeight += personalityW;
        weighted += personalityW * personalityMatch;
        addReason(reasons, "作息匹配", personalityMatch);

        double score = totalWeight <= 0 ? 0 : (weighted / totalWeight) * 100.0;
        score = Math.round(score * 10.0) / 10.0;

        if (reasons.isEmpty()) {
            reasons.add("按默认规则为你匹配");
        }

        return ScoreResult.accepted(score, reasons);
    }

    private void addReason(List<String> reasons, String label, double match) {
        if (match >= 0.85) {
            reasons.add(label + "：优势");
        } else if (match <= 0.5) {
            reasons.add(label + "：需磨合");
        }
    }

    private double matchActivity(UserAdoptionProfile p, CatTraits t) {
        int hours = safeInt(p.getDailyCompanionshipHours(), 0);
        int userLevel;
        if (hours >= 5) {
            userLevel = 5;
        } else if (hours >= 3) {
            userLevel = 4;
        } else if (hours >= 2) {
            userLevel = 3;
        } else if (hours >= 1) {
            userLevel = 2;
        } else {
            userLevel = 1;
        }

        int catLevel = clamp(safeInt(t.getActivityLevel(), 3), 1, 5);
        int diff = Math.abs(userLevel - catLevel);
        return 1.0 - (diff / 4.0);
    }

    private double matchAffection(UserAdoptionProfile p, CatTraits t) {
        String pref = p.getPersonalityPreference() == null ? "ANY" : p.getPersonalityPreference();
        int affection = clamp(safeInt(t.getAffectionLevel(), 3), 1, 5);
        double normalized = (affection - 1) / 4.0;
        if ("CUDDLY".equalsIgnoreCase(pref)) {
            return normalized;
        }
        if ("INDEPENDENT".equalsIgnoreCase(pref)) {
            return 1.0 - normalized;
        }
        return 0.8;
    }

    private double matchShedding(UserAdoptionProfile p, CatTraits t) {
        int maxShedding = clamp(safeInt(p.getMaxSheddingLevel(), 5), 1, 5);
        int shedding = clamp(safeInt(t.getSheddingLevel(), 3), 1, 5);
        if (shedding <= maxShedding) {
            return 1.0;
        }
        return 1.0 - ((shedding - maxShedding) / 4.0);
    }

    private double matchSpace(UserAdoptionProfile p, CatTraits t) {
        int homeSize = clamp(safeInt(p.getHomeSizeLevel(), 2), 1, 3);
        int need = clamp(safeInt(t.getSpaceNeedLevel(), 2), 1, 3);
        if (homeSize >= need) {
            return 1.0;
        }
        return 1.0 - ((need - homeSize) / 2.0);
    }

    private double matchBeginner(UserAdoptionProfile p, CatTraits t) {
        int exp = clamp(safeInt(p.getExperienceLevel(), 0), 0, 2);
        boolean beginnerFriendly = Boolean.TRUE.equals(t.getBeginnerFriendly());
        if (exp == 0 && !beginnerFriendly) {
            return 0.0;
        }
        return 1.0;
    }

    private double matchSchedule(UserAdoptionProfile p, CatTraits t) {
        String schedule = p.getWorkSchedule() == null ? "DAY" : p.getWorkSchedule();
        boolean nocturnal = Boolean.TRUE.equals(t.getNocturnal());
        if ("NIGHT".equalsIgnoreCase(schedule)) {
            return nocturnal ? 1.0 : 0.6;
        }
        return nocturnal ? 0.6 : 1.0;
    }

    private CatTraits defaultTraits(Integer catId) {
        CatTraits t = new CatTraits();
        t.setCatId(catId);
        t.setPersonality("ANY");
        t.setActivityLevel(3);
        t.setAffectionLevel(3);
        t.setChildFriendly(true);
        t.setPetFriendly(true);
        t.setSheddingLevel(3);
        t.setNocturnal(false);
        t.setSpaceNeedLevel(2);
        t.setBeginnerFriendly(true);
        t.setSpecialCareNeeded(false);
        t.setEnvironmentPreference("ANY");
        return t;
    }

    private int safeInt(Integer value, int def) {
        return value == null ? def : value;
    }

    private int clamp(int value, int min, int max) {
        if (value < min) {
            return min;
        }
        if (value > max) {
            return max;
        }
        return value;
    }

    private record Scored(Animals cat, Double score, List<String> reasons) {
    }

    private static class ScoreResult {
        private boolean rejected;
        private double score;
        private List<String> reasons;

        static ScoreResult rejected(String reason) {
            ScoreResult sr = new ScoreResult();
            sr.rejected = true;
            sr.score = 0;
            sr.reasons = List.of(reason);
            return sr;
        }

        static ScoreResult accepted(double score, List<String> reasons) {
            ScoreResult sr = new ScoreResult();
            sr.rejected = false;
            sr.score = score;
            sr.reasons = reasons;
            return sr;
        }
    }
}
