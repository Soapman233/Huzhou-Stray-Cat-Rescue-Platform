package com.hzsca.entity;

import java.time.LocalDateTime;

public class UserAdoptionProfile {
    private Long userId;
    private String residenceCity;
    private String housingType;
    private Integer homeSizeLevel;
    private Boolean hasChildren;
    private Boolean hasOtherPets;
    private Boolean allergy;
    private Integer maxSheddingLevel;
    private Integer dailyCompanionshipHours;
    private String workSchedule;
    private Integer budgetLevel;
    private Integer experienceLevel;
    private String personalityPreference;
    private Boolean acceptSpecialCare;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getResidenceCity() {
        return residenceCity;
    }

    public void setResidenceCity(String residenceCity) {
        this.residenceCity = residenceCity;
    }

    public String getHousingType() {
        return housingType;
    }

    public void setHousingType(String housingType) {
        this.housingType = housingType;
    }

    public Integer getHomeSizeLevel() {
        return homeSizeLevel;
    }

    public void setHomeSizeLevel(Integer homeSizeLevel) {
        this.homeSizeLevel = homeSizeLevel;
    }

    public Boolean getHasChildren() {
        return hasChildren;
    }

    public void setHasChildren(Boolean hasChildren) {
        this.hasChildren = hasChildren;
    }

    public Boolean getHasOtherPets() {
        return hasOtherPets;
    }

    public void setHasOtherPets(Boolean hasOtherPets) {
        this.hasOtherPets = hasOtherPets;
    }

    public Boolean getAllergy() {
        return allergy;
    }

    public void setAllergy(Boolean allergy) {
        this.allergy = allergy;
    }

    public Integer getMaxSheddingLevel() {
        return maxSheddingLevel;
    }

    public void setMaxSheddingLevel(Integer maxSheddingLevel) {
        this.maxSheddingLevel = maxSheddingLevel;
    }

    public Integer getDailyCompanionshipHours() {
        return dailyCompanionshipHours;
    }

    public void setDailyCompanionshipHours(Integer dailyCompanionshipHours) {
        this.dailyCompanionshipHours = dailyCompanionshipHours;
    }

    public String getWorkSchedule() {
        return workSchedule;
    }

    public void setWorkSchedule(String workSchedule) {
        this.workSchedule = workSchedule;
    }

    public Integer getBudgetLevel() {
        return budgetLevel;
    }

    public void setBudgetLevel(Integer budgetLevel) {
        this.budgetLevel = budgetLevel;
    }

    public Integer getExperienceLevel() {
        return experienceLevel;
    }

    public void setExperienceLevel(Integer experienceLevel) {
        this.experienceLevel = experienceLevel;
    }

    public String getPersonalityPreference() {
        return personalityPreference;
    }

    public void setPersonalityPreference(String personalityPreference) {
        this.personalityPreference = personalityPreference;
    }

    public Boolean getAcceptSpecialCare() {
        return acceptSpecialCare;
    }

    public void setAcceptSpecialCare(Boolean acceptSpecialCare) {
        this.acceptSpecialCare = acceptSpecialCare;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }
}
