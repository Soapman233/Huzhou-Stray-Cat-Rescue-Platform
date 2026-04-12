package com.hzsca.entity;

import java.math.BigDecimal;

public class AdoptionForm {
    private Long requestId;
    private BigDecimal familyIncome;
    private Boolean hasOwnedPets;
    private Boolean smoker;
    private Boolean asthma;
    private Boolean allergy;
    private String livingSpace;
    private Boolean petFriendly;
    private Integer workHours;
    private String rentOrOwn;
    private Integer familyMembers;
    private Boolean childInHome;
    private Boolean acceptReturnVisit;
    private String remarks;

    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    public BigDecimal getFamilyIncome() {
        return familyIncome;
    }

    public void setFamilyIncome(BigDecimal familyIncome) {
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

    public Integer getWorkHours() {
        return workHours;
    }

    public void setWorkHours(Integer workHours) {
        this.workHours = workHours;
    }

    public String getRentOrOwn() {
        return rentOrOwn;
    }

    public void setRentOrOwn(String rentOrOwn) {
        this.rentOrOwn = rentOrOwn;
    }

    public Integer getFamilyMembers() {
        return familyMembers;
    }

    public void setFamilyMembers(Integer familyMembers) {
        this.familyMembers = familyMembers;
    }

    public Boolean getChildInHome() {
        return childInHome;
    }

    public void setChildInHome(Boolean childInHome) {
        this.childInHome = childInHome;
    }

    public Boolean getAcceptReturnVisit() {
        return acceptReturnVisit;
    }

    public void setAcceptReturnVisit(Boolean acceptReturnVisit) {
        this.acceptReturnVisit = acceptReturnVisit;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
