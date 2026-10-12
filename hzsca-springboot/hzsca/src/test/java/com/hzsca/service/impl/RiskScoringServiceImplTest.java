package com.hzsca.service.impl;

import com.hzsca.entity.AdoptionForm;
import com.hzsca.service.RiskScoringService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 领养风险评分算法单元测试。
 *
 * <p>评分规则（100 分基准，扣分制）：
 * 过敏 −25、哮喘 −15、吸烟 −10、工时≥12h −12 / ≥10h −8 / ≤6h +3、
 * 家庭收入&lt;3000 −10 / ≥8000 +5、无养宠经验 −5 / 有 +2、接受回访 +5 / 拒绝 −8；
 * clamp 到 [0,100]；≥80 LOW、≥60 MEDIUM、否则 HIGH。</p>
 *
 * <p>该测试为纯单元测试，不依赖 Spring 上下文与数据库。</p>
 */
class RiskScoringServiceImplTest {

    private final RiskScoringService service = new RiskScoringServiceImpl();

    /** 构造一个「全空」表单，即所有加减分项均不触发 */
    private AdoptionForm emptyForm() {
        return new AdoptionForm();
    }

    private RiskScoringService.ScoreResult score(AdoptionForm form) {
        return service.score(form);
    }

    // ---------------- 基线 ----------------

    @Test
    @DisplayName("R1/R22 全空表单：满分 100，风险 LOW")
    void emptyForm_scoresFullMarks() {
        RiskScoringService.ScoreResult r = score(emptyForm());
        assertThat(r.score).isEqualTo(100);
        assertThat(r.riskLevel).isEqualTo("LOW");
        assertThat(r.reasons).isEqualTo("无明显风险项");
    }

    @Test
    @DisplayName("R21 表单为 null：返回 60 分、MEDIUM")
    void nullForm_returnsMedium() {
        RiskScoringService.ScoreResult r = score(null);
        assertThat(r.score).isEqualTo(60);
        assertThat(r.riskLevel).isEqualTo("MEDIUM");
        assertThat(r.reasons).contains("未填写申请表");
    }

    // ---------------- 单项扣分 ----------------

    @Test
    @DisplayName("R2 过敏：100−25=75，MEDIUM")
    void allergy_deducts25() {
        AdoptionForm f = emptyForm();
        f.setAllergy(true);
        RiskScoringService.ScoreResult r = score(f);
        assertThat(r.score).isEqualTo(75);
        assertThat(r.riskLevel).isEqualTo("MEDIUM");
        assertThat(r.reasons).contains("过敏");
    }

    @Test
    @DisplayName("R3 哮喘：100−15=85，LOW")
    void asthma_deducts15() {
        AdoptionForm f = emptyForm();
        f.setAsthma(true);
        assertThat(score(f).score).isEqualTo(85);
        assertThat(score(f).riskLevel).isEqualTo("LOW");
    }

    @Test
    @DisplayName("R4 吸烟：100−10=90，LOW")
    void smoker_deducts10() {
        AdoptionForm f = emptyForm();
        f.setSmoker(true);
        assertThat(score(f).score).isEqualTo(90);
        assertThat(score(f).riskLevel).isEqualTo("LOW");
    }

    @Test
    @DisplayName("R5 工时≥12h：100−12=88")
    void workHours12_deducts12() {
        AdoptionForm f = emptyForm();
        f.setWorkHours(12);
        assertThat(score(f).score).isEqualTo(88);
    }

    @Test
    @DisplayName("R6 工时=10h：100−8=92")
    void workHours10_deducts8() {
        AdoptionForm f = emptyForm();
        f.setWorkHours(10);
        assertThat(score(f).score).isEqualTo(92);
    }

    @Test
    @DisplayName("R7 工时≤6h：100+3=103，上界截断为 100")
    void workHours6_adds3_thenClamped() {
        AdoptionForm f = emptyForm();
        f.setWorkHours(6);
        assertThat(score(f).score).isEqualTo(100);
    }

    @Test
    @DisplayName("R8 家庭收入 2500：100−10=90")
    void lowIncome_deducts10() {
        AdoptionForm f = emptyForm();
        f.setFamilyIncome(new BigDecimal("2500"));
        assertThat(score(f).score).isEqualTo(90);
    }

    @Test
    @DisplayName("R9 家庭收入 9000：100+5=105，上界截断为 100")
    void highIncome_adds5_thenClamped() {
        AdoptionForm f = emptyForm();
        f.setFamilyIncome(new BigDecimal("9000"));
        assertThat(score(f).score).isEqualTo(100);
    }

    @Test
    @DisplayName("R10 无养宠经验：100−5=95")
    void noPetExperience_deducts5() {
        AdoptionForm f = emptyForm();
        f.setHasOwnedPets(false);
        assertThat(score(f).score).isEqualTo(95);
    }

    @Test
    @DisplayName("R11 有养宠经验：100+2=102，上界截断为 100")
    void petExperience_adds2_thenClamped() {
        AdoptionForm f = emptyForm();
        f.setHasOwnedPets(true);
        assertThat(score(f).score).isEqualTo(100);
    }

    @Test
    @DisplayName("R12 拒绝回访：100−8=92")
    void refuseReturnVisit_deducts8() {
        AdoptionForm f = emptyForm();
        f.setAcceptReturnVisit(false);
        assertThat(score(f).score).isEqualTo(92);
    }

    @Test
    @DisplayName("R13 接受回访：100+5=105，上界截断为 100")
    void acceptReturnVisit_adds5_thenClamped() {
        AdoptionForm f = emptyForm();
        f.setAcceptReturnVisit(true);
        assertThat(score(f).score).isEqualTo(100);
    }

    // ---------------- 组合与边界 ----------------

    @Test
    @DisplayName("R14 高风险组合：过敏+哮喘+吸烟+12h+低收入+无经验+拒绝回访 = 15 分，HIGH")
    void highRiskCombination_scores15() {
        AdoptionForm f = emptyForm();
        f.setAllergy(true);            // -25
        f.setAsthma(true);             // -15
        f.setSmoker(true);             // -10
        f.setWorkHours(12);            // -12
        f.setFamilyIncome(new BigDecimal("2000")); // -10
        f.setHasOwnedPets(false);      // -5
        f.setAcceptReturnVisit(false); // -8
        RiskScoringService.ScoreResult r = score(f);
        assertThat(r.score).isEqualTo(15);
        assertThat(r.riskLevel).isEqualTo("HIGH");
    }

    @Test
    @DisplayName("R15 恰好 80 分：吸烟+低收入 → LOW 下界（实现为 score>=80 即 LOW）")
    void exactly80_isLow() {
        AdoptionForm f = emptyForm();
        f.setSmoker(true);                          // -10
        f.setFamilyIncome(new BigDecimal("2000"));  // -10
        RiskScoringService.ScoreResult r = score(f);
        assertThat(r.score).isEqualTo(80);
        assertThat(r.riskLevel).isEqualTo("LOW");
    }

    @Test
    @DisplayName("R16 恰好 60 分：过敏+哮喘 → MEDIUM 上界")
    void exactly60_isMedium() {
        AdoptionForm f = emptyForm();
        f.setAllergy(true); // -25
        f.setAsthma(true);  // -15
        RiskScoringService.ScoreResult r = score(f);
        assertThat(r.score).isEqualTo(60);
        assertThat(r.riskLevel).isEqualTo("MEDIUM");
    }

    @Test
    @DisplayName("R17 恰好 59 分：过敏+10h+拒绝回访 → HIGH 上界")
    void exactly59_isHigh() {
        AdoptionForm f = emptyForm();
        f.setAllergy(true);            // -25
        f.setWorkHours(10);            // -8
        f.setAcceptReturnVisit(false); // -8
        RiskScoringService.ScoreResult r = score(f);
        assertThat(r.score).isEqualTo(59);
        assertThat(r.riskLevel).isEqualTo("HIGH");
    }

    @Test
    @DisplayName("R18 恰好 79 分：10h+拒绝回访+无经验 → MEDIUM")
    void exactly79_isMedium() {
        AdoptionForm f = emptyForm();
        f.setWorkHours(10);            // -8
        f.setAcceptReturnVisit(false); // -8
        f.setHasOwnedPets(false);      // -5
        RiskScoringService.ScoreResult r = score(f);
        assertThat(r.score).isEqualTo(79);
        assertThat(r.riskLevel).isEqualTo("MEDIUM");
    }

    @Test
    @DisplayName("R19 全加分项叠加：115 → 上界截断为 100，LOW")
    void allPositiveBonuses_clampedTo100() {
        AdoptionForm f = emptyForm();
        f.setWorkHours(6);                 // +3
        f.setFamilyIncome(new BigDecimal("9000")); // +5
        f.setHasOwnedPets(true);           // +2
        f.setAcceptReturnVisit(true);      // +5
        RiskScoringService.ScoreResult r = score(f);
        assertThat(r.score).isEqualTo(100);
        assertThat(r.riskLevel).isEqualTo("LOW");
    }

    @Test
    @DisplayName("评分恒在 [0,100] 区间内，且 reasons 非空")
    void scoreAlwaysWithinRange() {
        AdoptionForm f = emptyForm();
        f.setAllergy(true);
        f.setAsthma(true);
        f.setSmoker(true);
        f.setWorkHours(14);
        f.setFamilyIncome(new BigDecimal("1000"));
        f.setHasOwnedPets(false);
        f.setAcceptReturnVisit(false);
        RiskScoringService.ScoreResult r = score(f);
        assertThat(r.score).isBetween(0, 100);
        assertThat(r.reasons).isNotBlank();
    }

    @Test
    @DisplayName("风险等级只可能取 LOW / MEDIUM / HIGH")
    void riskLevelIsAlwaysKnownValue() {
        AdoptionForm f = emptyForm();
        f.setAllergy(true);
        assertThat(score(f).riskLevel).isIn("LOW", "MEDIUM", "HIGH");
        assertThat(score(emptyForm()).riskLevel).isIn("LOW", "MEDIUM", "HIGH");
        assertThat(score(null).riskLevel).isIn("LOW", "MEDIUM", "HIGH");
    }
}
