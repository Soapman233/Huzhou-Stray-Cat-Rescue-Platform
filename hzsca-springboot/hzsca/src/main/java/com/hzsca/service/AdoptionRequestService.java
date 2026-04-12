package com.hzsca.service;

import com.hzsca.common.Result;
import com.hzsca.entity.AdoptionForm;

public interface AdoptionRequestService {
    Result submit(Long applicantId, String applicantType, Integer animalId, AdoptionForm form);

    Result myRequests(Long applicantId, String applicantType);

    Result myRequestDetail(Long applicantId, String applicantType, Long requestId);

    Result cancel(Long applicantId, String applicantType, Long requestId);

    Result adminList(String status, Integer page, Integer size);

    Result adminDetail(Long requestId);

    Result approve(Long adminId, Long requestId, String reason);

    Result reject(Long adminId, Long requestId, String reason);
}
