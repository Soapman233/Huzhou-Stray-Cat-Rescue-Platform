package com.hzsca.controller;

import com.hzsca.common.Result;
import com.hzsca.entity.AdoptionForm;
import com.hzsca.security.SecurityUtils;
import com.hzsca.service.AdoptionRequestService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/adoption-requests")
public class AdoptionRequestController {
    private final AdoptionRequestService adoptionRequestService;

    public AdoptionRequestController(AdoptionRequestService adoptionRequestService) {
        this.adoptionRequestService = adoptionRequestService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public Result submit(@RequestBody SubmitRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        String type = SecurityUtils.getCurrentPrincipalType();
        AdoptionForm form = new AdoptionForm();
        if (request != null) {
            form.setFamilyIncome(request.familyIncome);
            form.setHasOwnedPets(request.hasOwnedPets);
            form.setSmoker(request.smoker);
            form.setAsthma(request.asthma);
            form.setAllergy(request.allergy);
            form.setLivingSpace(request.livingSpace);
            form.setPetFriendly(request.petFriendly);
            form.setWorkHours(request.workHours);
            form.setRentOrOwn(request.rentOrOwn);
            form.setFamilyMembers(request.familyMembers);
            form.setChildInHome(request.childInHome);
            form.setAcceptReturnVisit(request.acceptReturnVisit);
            form.setRemarks(request.remarks);
        }
        Integer animalId = request == null ? null : request.animalId;
        return adoptionRequestService.submit(userId, type, animalId, form);
    }

    @GetMapping("/my")
    public Result myRequests() {
        Long userId = SecurityUtils.getCurrentUserId();
        String type = SecurityUtils.getCurrentPrincipalType();
        return adoptionRequestService.myRequests(userId, type);
    }

    @GetMapping("/{id}")
    public Result myDetail(@PathVariable("id") Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        String type = SecurityUtils.getCurrentPrincipalType();
        return adoptionRequestService.myRequestDetail(userId, type, id);
    }

    @PostMapping("/{id}/cancel")
    public Result cancel(@PathVariable("id") Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        String type = SecurityUtils.getCurrentPrincipalType();
        return adoptionRequestService.cancel(userId, type, id);
    }

    public static class SubmitRequest {
        public Integer animalId;
        public java.math.BigDecimal familyIncome;
        public Boolean hasOwnedPets;
        public Boolean smoker;
        public Boolean asthma;
        public Boolean allergy;
        public String livingSpace;
        public Boolean petFriendly;
        public Integer workHours;
        public String rentOrOwn;
        public Integer familyMembers;
        public Boolean childInHome;
        public Boolean acceptReturnVisit;
        public String remarks;
    }
}

