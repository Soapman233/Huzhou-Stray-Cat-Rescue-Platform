package com.hzsca.entity;

import java.time.LocalDateTime;

public class CatTraits {
    private Integer catId;
    private String personality;
    private Integer activityLevel;
    private Integer affectionLevel;
    private Boolean childFriendly;
    private Boolean petFriendly;
    private Integer sheddingLevel;
    private Boolean nocturnal;
    private Integer spaceNeedLevel;
    private Boolean beginnerFriendly;
    private Boolean specialCareNeeded;
    private String environmentPreference;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    public Integer getCatId() {
        return catId;
    }

    public void setCatId(Integer catId) {
        this.catId = catId;
    }

    public String getPersonality() {
        return personality;
    }

    public void setPersonality(String personality) {
        this.personality = personality;
    }

    public Integer getActivityLevel() {
        return activityLevel;
    }

    public void setActivityLevel(Integer activityLevel) {
        this.activityLevel = activityLevel;
    }

    public Integer getAffectionLevel() {
        return affectionLevel;
    }

    public void setAffectionLevel(Integer affectionLevel) {
        this.affectionLevel = affectionLevel;
    }

    public Boolean getChildFriendly() {
        return childFriendly;
    }

    public void setChildFriendly(Boolean childFriendly) {
        this.childFriendly = childFriendly;
    }

    public Boolean getPetFriendly() {
        return petFriendly;
    }

    public void setPetFriendly(Boolean petFriendly) {
        this.petFriendly = petFriendly;
    }

    public Integer getSheddingLevel() {
        return sheddingLevel;
    }

    public void setSheddingLevel(Integer sheddingLevel) {
        this.sheddingLevel = sheddingLevel;
    }

    public Boolean getNocturnal() {
        return nocturnal;
    }

    public void setNocturnal(Boolean nocturnal) {
        this.nocturnal = nocturnal;
    }

    public Integer getSpaceNeedLevel() {
        return spaceNeedLevel;
    }

    public void setSpaceNeedLevel(Integer spaceNeedLevel) {
        this.spaceNeedLevel = spaceNeedLevel;
    }

    public Boolean getBeginnerFriendly() {
        return beginnerFriendly;
    }

    public void setBeginnerFriendly(Boolean beginnerFriendly) {
        this.beginnerFriendly = beginnerFriendly;
    }

    public Boolean getSpecialCareNeeded() {
        return specialCareNeeded;
    }

    public void setSpecialCareNeeded(Boolean specialCareNeeded) {
        this.specialCareNeeded = specialCareNeeded;
    }

    public String getEnvironmentPreference() {
        return environmentPreference;
    }

    public void setEnvironmentPreference(String environmentPreference) {
        this.environmentPreference = environmentPreference;
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
