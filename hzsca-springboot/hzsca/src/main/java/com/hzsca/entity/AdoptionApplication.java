package com.hzsca.entity;

public class AdoptionApplication {
    private Integer assessmentId;  // 评估ID
    private Integer adopterId;     // 关联的领养人ID
    private String name;          // 领养人姓名
    private Double familyIncome;  // 家庭收入，改为 Double 类型
    private Boolean hasOwnedPets; // 是否养过小动物
    private Boolean smoker;       // 家中是否有人吸烟
    private Boolean asthma;       // 家中是否有人哮喘
    private Boolean allergy;      // 动物毛过敏
    private String livingSpace;   // 居住空间
    private Boolean petFriendly;  // 是否宠物友好
    private String createTime;    // 创建时间
    private String status;        // 申请状态：PENDING-待审核, APPROVED-已通过, REJECTED-已拒绝

    public AdoptionApplication() {
    }

    public Integer getAssessmentId() {
        return assessmentId;
    }

    public void setAssessmentId(Integer assessmentId) {
        this.assessmentId = assessmentId;
    }

    public Integer getAdopterId() {
        return adopterId;
    }

    public void setAdopterId(Integer adopterId) {
        this.adopterId = adopterId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getFamilyIncome() {
        return familyIncome;
    }

    public void setFamilyIncome(Double familyIncome) {
        this.familyIncome = familyIncome;
    }

    public Boolean getHasOwnedPets() {
        return hasOwnedPets;
    }

    public void setHasOwnedPets(Boolean hasOwnedPets) {
        this.hasOwnedPets = hasOwnedPets;
    }

    public Boolean getSmoker() {
        return smoker;
    }

    public void setSmoker(Boolean smoker) {
        this.smoker = smoker;
    }

    public Boolean getAsthma() {
        return asthma;
    }

    public void setAsthma(Boolean asthma) {
        this.asthma = asthma;
    }

    public Boolean getAllergy() {
        return allergy;
    }

    public void setAllergy(Boolean allergy) {
        this.allergy = allergy;
    }

    public String getLivingSpace() {
        return livingSpace;
    }

    public void setLivingSpace(String livingSpace) {
        this.livingSpace = livingSpace;
    }

    public Boolean getPetFriendly() {
        return petFriendly;
    }

    public void setPetFriendly(Boolean petFriendly) {
        this.petFriendly = petFriendly;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}