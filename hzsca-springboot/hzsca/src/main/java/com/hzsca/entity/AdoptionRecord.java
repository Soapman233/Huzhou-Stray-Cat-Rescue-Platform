package com.hzsca.entity;

import java.time.LocalDateTime;

public class AdoptionRecord {
    private Long id;
    private Long requestId;
    private Integer animalId;
    private Long wxUserId;
    private LocalDateTime adoptTime;
    private String contractUrl;
    private String followupPlan;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    public Integer getAnimalId() {
        return animalId;
    }

    public void setAnimalId(Integer animalId) {
        this.animalId = animalId;
    }

    public Long getWxUserId() {
        return wxUserId;
    }

    public void setWxUserId(Long wxUserId) {
        this.wxUserId = wxUserId;
    }

    public LocalDateTime getAdoptTime() {
        return adoptTime;
    }

    public void setAdoptTime(LocalDateTime adoptTime) {
        this.adoptTime = adoptTime;
    }

    public String getContractUrl() {
        return contractUrl;
    }

    public void setContractUrl(String contractUrl) {
        this.contractUrl = contractUrl;
    }

    public String getFollowupPlan() {
        return followupPlan;
    }

    public void setFollowupPlan(String followupPlan) {
        this.followupPlan = followupPlan;
    }
}
