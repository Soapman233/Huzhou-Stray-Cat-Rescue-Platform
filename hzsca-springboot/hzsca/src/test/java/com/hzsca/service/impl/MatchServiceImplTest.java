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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

/**
 * 猫咪智能匹配算法单元测试。
 *
 * <p>算法为「硬性排除 + 六维加权打分」两阶段模型：
 * 先按过敏×掉毛、儿童×不亲童、他宠×不亲宠、特殊照护、居住空间做一票否决，
 * 再对活动量、亲人度、掉毛容忍、空间、新手友好、作息六维加权平均 ×100。</p>
 *
 * <p>使用 Mockito 隔离 Mapper，不依赖数据库与 Spring 上下文。</p>
 */
@ExtendWith(MockitoExtension.class)
class MatchServiceImplTest {

    @Mock
    private UserAdoptionProfileMapper profileMapper;
    @Mock
    private AnimalsMapper animalsMapper;
    @Mock
    private CatTraitsMapper catTraitsMapper;
    @Mock
    private MatchWeightMapper matchWeightMapper;

    @InjectMocks
    private MatchServiceImpl matchService;

    // ---------------- 构造工具 ----------------

    private Animals cat(int id) {
        Animals a = new Animals();
        a.setAnimalId(id);
        a.setName("猫" + id);
        a.setSpecies("猫");
        a.setStatus("ADOPTABLE");
        return a;
    }

    /** 一份「不会触发任何硬性排除」的用户档案基线 */
    private UserAdoptionProfile safeProfile() {
        UserAdoptionProfile p = new UserAdoptionProfile();
        p.setUserId(1L);
        p.setResidenceCity("湖州");
        p.setHousingType("整租");
        p.setHomeSizeLevel(3);
        p.setHasChildren(false);
        p.setHasOtherPets(false);
        p.setAllergy(false);
        p.setMaxSheddingLevel(5);
        p.setDailyCompanionshipHours(5);
        p.setWorkSchedule("DAY");
        p.setBudgetLevel(2);
        p.setExperienceLevel(1);
        p.setPersonalityPreference("CUDDLY");
        p.setAcceptSpecialCare(true);
        return p;
    }

    /** 一份「各项都最优」的猫咪特征 */
    private CatTraits perfectTraits(int catId) {
        CatTraits t = new CatTraits();
        t.setCatId(catId);
        t.setPersonality("CUDDLY");
        t.setActivityLevel(5);
        t.setAffectionLevel(5);
        t.setChildFriendly(true);
        t.setPetFriendly(true);
        t.setSheddingLevel(1);
        t.setNocturnal(false);
        t.setSpaceNeedLevel(1);
        t.setBeginnerFriendly(true);
        t.setSpecialCareNeeded(false);
        t.setEnvironmentPreference("ANY");
        return t;
    }

    private MatchWeight weight(String key, double value) {
        MatchWeight w = new MatchWeight();
        w.setWeightKey(key);
        w.setWeightValue(value);
        return w;
    }

    /** 默认：权重表为空（走内置默认权重），traits 由调用方按需 mock */
    private void stubDefaults(List<Animals> cats, List<CatTraits> traits) {
        when(animalsMapper.getAdoptableCats()).thenReturn(cats);
        lenient().when(catTraitsMapper.selectByCatIds(any())).thenReturn(traits);
        lenient().when(matchWeightMapper.selectAll()).thenReturn(Collections.emptyList());
    }

    // ---------------- 空入参 ----------------

    @Test
    @DisplayName("M1 userId 为 null：返回空列表")
    void nullUserId_returnsEmpty() {
        assertThat(matchService.recommend(null, 10)).isEmpty();
    }

    @Test
    @DisplayName("M2 用户未填写领养档案：返回空列表")
    void noProfile_returnsEmpty() {
        when(profileMapper.selectByUserId(anyLong())).thenReturn(null);
        assertThat(matchService.recommend(1L, 10)).isEmpty();
    }

    @Test
    @DisplayName("M3 没有可领养猫咪：返回空列表")
    void noAdoptableCats_returnsEmpty() {
        when(profileMapper.selectByUserId(anyLong())).thenReturn(safeProfile());
        when(animalsMapper.getAdoptableCats()).thenReturn(Collections.emptyList());
        assertThat(matchService.recommend(1L, 10)).isEmpty();
    }

    // ---------------- 硬性排除 ----------------

    @Test
    @DisplayName("M4 过敏且猫咪掉毛超过容忍上限：被排除")
    void allergyWithExcessiveShedding_isRejected() {
        UserAdoptionProfile p = safeProfile();
        p.setAllergy(true);
        p.setMaxSheddingLevel(1);
        when(profileMapper.selectByUserId(anyLong())).thenReturn(p);
        CatTraits t = perfectTraits(1);
        t.setSheddingLevel(3);
        stubDefaults(List.of(cat(1)), List.of(t));

        assertThat(matchService.recommend(1L, 10)).isEmpty();
    }

    @Test
    @DisplayName("M5 家中有孩子但猫咪不亲童：被排除")
    void hasChildrenWithChildUnfriendlyCat_isRejected() {
        UserAdoptionProfile p = safeProfile();
        p.setHasChildren(true);
        when(profileMapper.selectByUserId(anyLong())).thenReturn(p);
        CatTraits t = perfectTraits(1);
        t.setChildFriendly(false);
        stubDefaults(List.of(cat(1)), List.of(t));

        assertThat(matchService.recommend(1L, 10)).isEmpty();
    }

    @Test
    @DisplayName("M6 家中有其他宠物但猫咪不亲宠：被排除")
    void hasOtherPetsWithPetUnfriendlyCat_isRejected() {
        UserAdoptionProfile p = safeProfile();
        p.setHasOtherPets(true);
        when(profileMapper.selectByUserId(anyLong())).thenReturn(p);
        CatTraits t = perfectTraits(1);
        t.setPetFriendly(false);
        stubDefaults(List.of(cat(1)), List.of(t));

        assertThat(matchService.recommend(1L, 10)).isEmpty();
    }

    @Test
    @DisplayName("M7 用户不接受特殊照护但猫咪需要特殊照护：被排除")
    void refusesSpecialCare_isRejected() {
        UserAdoptionProfile p = safeProfile();
        p.setAcceptSpecialCare(false);
        when(profileMapper.selectByUserId(anyLong())).thenReturn(p);
        CatTraits t = perfectTraits(1);
        t.setSpecialCareNeeded(true);
        stubDefaults(List.of(cat(1)), List.of(t));

        assertThat(matchService.recommend(1L, 10)).isEmpty();
    }

    @Test
    @DisplayName("M8 居住空间小于猫咪空间需求：被排除")
    void insufficientSpace_isRejected() {
        UserAdoptionProfile p = safeProfile();
        p.setHomeSizeLevel(1);
        when(profileMapper.selectByUserId(anyLong())).thenReturn(p);
        CatTraits t = perfectTraits(1);
        t.setSpaceNeedLevel(3);
        stubDefaults(List.of(cat(1)), List.of(t));

        assertThat(matchService.recommend(1L, 10)).isEmpty();
    }

    @Test
    @DisplayName("M4b 过敏但猫咪掉毛在容忍范围内：不被排除")
    void allergyWithinSheddingTolerance_isKept() {
        UserAdoptionProfile p = safeProfile();
        p.setAllergy(true);
        p.setMaxSheddingLevel(4);
        when(profileMapper.selectByUserId(anyLong())).thenReturn(p);
        CatTraits t = perfectTraits(1);
        t.setSheddingLevel(3);
        stubDefaults(List.of(cat(1)), List.of(t));

        assertThat(matchService.recommend(1L, 10)).hasSize(1);
    }

    // ---------------- 打分 ----------------

    @Test
    @DisplayName("M9 六维全部最优：得分 100.0")
    void perfectMatch_scores100() {
        when(profileMapper.selectByUserId(anyLong())).thenReturn(safeProfile());
        stubDefaults(List.of(cat(1)), List.of(perfectTraits(1)));

        List<MatchService.Recommendation> result = matchService.recommend(1L, 10);
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getScore()).isEqualTo(100.0);
    }

    @Test
    @DisplayName("M10 六维全部最差（未触发排除）：得分 27.6")
    void worstMatch_scores27Point6() {
        UserAdoptionProfile p = safeProfile();
        p.setDailyCompanionshipHours(0);   // 用户活跃度 1
        p.setMaxSheddingLevel(1);          // 掉毛容忍最低
        p.setWorkSchedule("NIGHT");
        p.setExperienceLevel(0);
        when(profileMapper.selectByUserId(anyLong())).thenReturn(p);

        CatTraits t = perfectTraits(1);
        t.setActivityLevel(5);             // 与用户活跃度差距最大
        t.setAffectionLevel(1);            // 用户偏好黏人，猫最独立
        t.setSheddingLevel(5);             // 掉毛远超容忍（未开过敏，不触发排除）
        t.setBeginnerFriendly(false);      // 新手不友好
        t.setNocturnal(false);             // 夜班用户 × 非夜行猫
        stubDefaults(List.of(cat(1)), List.of(t));

        List<MatchService.Recommendation> result = matchService.recommend(1L, 10);
        assertThat(result).hasSize(1);
        // activity 0.0 / affection 0.0 / shedding 0.0 / space 1.0 / beginner 0.0 / schedule 0.6
        // = (0 + 0 + 0 + 1.0 + 0 + 0.6) / 5.8 * 100 = 27.586... → 27.6
        assertThat(result.get(0).getScore()).isEqualTo(27.6);
    }

    @Test
    @DisplayName("M11 权重表全部为 0：总权重为 0，得分 0.0")
    void zeroWeights_scoresZero() {
        when(profileMapper.selectByUserId(anyLong())).thenReturn(safeProfile());
        when(animalsMapper.getAdoptableCats()).thenReturn(List.of(cat(1)));
        when(catTraitsMapper.selectByCatIds(any())).thenReturn(List.of(perfectTraits(1)));
        when(matchWeightMapper.selectAll()).thenReturn(List.of(
                weight("activity", 0), weight("affection", 0), weight("shedding", 0),
                weight("space", 0), weight("beginner", 0), weight("personality", 0)));

        List<MatchService.Recommendation> result = matchService.recommend(1L, 10);
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getScore()).isEqualTo(0.0);
    }

    @Test
    @DisplayName("M17 权重表覆盖内置默认值：仅活动量权重生效")
    void weightOverrideFromTable_isApplied() {
        when(profileMapper.selectByUserId(anyLong())).thenReturn(safeProfile());
        when(animalsMapper.getAdoptableCats()).thenReturn(List.of(cat(1)));
        when(catTraitsMapper.selectByCatIds(any())).thenReturn(List.of(perfectTraits(1)));
        when(matchWeightMapper.selectAll()).thenReturn(List.of(
                weight("activity", 1.0), weight("affection", 0.0), weight("shedding", 0.0),
                weight("space", 0.0), weight("beginner", 0.0), weight("personality", 0.0)));

        List<MatchService.Recommendation> result = matchService.recommend(1L, 10);
        assertThat(result).hasSize(1);
        // 仅活动量权重 1.0，且该维度完全匹配 → 100.0
        assertThat(result.get(0).getScore()).isEqualTo(100.0);
    }

    @Test
    @DisplayName("M16 猫咪缺少特征记录：使用默认特征且不被排除")
    void missingTraits_fallsBackToDefaults() {
        when(profileMapper.selectByUserId(anyLong())).thenReturn(safeProfile());
        stubDefaults(List.of(cat(1)), Collections.emptyList());

        List<MatchService.Recommendation> result = matchService.recommend(1L, 10);
        assertThat(result).hasSize(1);
        // 默认特征：activity 3 / affection 3 / shedding 3 / space 2 / 新手友好 / 非夜行
        // 相对 safeProfile(CUDDLY, 陪伴5h, 空间3)：0.5+0.3+1.2+1.0+1.0+1.0 = 5.0 / 5.8 → 86.2
        assertThat(result.get(0).getScore()).isEqualTo(86.2);
    }

    @Test
    @DisplayName("M18 推荐理由：高分维度标记「优势」")
    void reasons_markAdvantages() {
        when(profileMapper.selectByUserId(anyLong())).thenReturn(safeProfile());
        stubDefaults(List.of(cat(1)), List.of(perfectTraits(1)));

        List<MatchService.Recommendation> result = matchService.recommend(1L, 10);
        assertThat(result.get(0).getReasons()).isNotEmpty();
        assertThat(result.get(0).getReasons()).anyMatch(r -> r.contains("优势"));
    }

    @Test
    @DisplayName("M15 结果按得分降序排列")
    void results_areSortedByScoreDesc() {
        when(profileMapper.selectByUserId(anyLong())).thenReturn(safeProfile());
        CatTraits best = perfectTraits(1);
        CatTraits worst = perfectTraits(2);
        worst.setActivityLevel(1);
        worst.setAffectionLevel(1);
        worst.setSheddingLevel(5);
        stubDefaults(List.of(cat(2), cat(1)), List.of(worst, best));

        List<MatchService.Recommendation> result = matchService.recommend(1L, 10);
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getScore()).isGreaterThanOrEqualTo(result.get(1).getScore());
        assertThat(result.get(0).getCat().getAnimalId()).isEqualTo(1);
    }

    // ---------------- limit 边界 ----------------

    @Test
    @DisplayName("M12 limit=0 时回退为默认 10 条")
    void limitZero_defaultsToTen() {
        when(profileMapper.selectByUserId(anyLong())).thenReturn(safeProfile());
        List<Animals> cats = new ArrayList<>();
        List<CatTraits> traits = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            cats.add(cat(i));
            traits.add(perfectTraits(i));
        }
        stubDefaults(cats, traits);

        assertThat(matchService.recommend(1L, 0)).hasSize(10);
    }

    @Test
    @DisplayName("M13 limit 为负数时回退为默认 10 条")
    void negativeLimit_defaultsToTen() {
        when(profileMapper.selectByUserId(anyLong())).thenReturn(safeProfile());
        List<Animals> cats = new ArrayList<>();
        List<CatTraits> traits = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            cats.add(cat(i));
            traits.add(perfectTraits(i));
        }
        stubDefaults(cats, traits);

        assertThat(matchService.recommend(1L, -5)).hasSize(10);
    }

    @Test
    @DisplayName("M14 limit 超过上限 50 时截断为 50 条")
    void excessiveLimit_cappedAtFifty() {
        when(profileMapper.selectByUserId(anyLong())).thenReturn(safeProfile());
        List<Animals> cats = new ArrayList<>();
        List<CatTraits> traits = new ArrayList<>();
        for (int i = 1; i <= 60; i++) {
            cats.add(cat(i));
            traits.add(perfectTraits(i));
        }
        stubDefaults(cats, traits);

        assertThat(matchService.recommend(1L, 60)).hasSize(50);
    }
}
