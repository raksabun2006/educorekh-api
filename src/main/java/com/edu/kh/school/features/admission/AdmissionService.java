package com.edu.kh.school.features.admission;

import com.edu.kh.school.features.admission.dto.AdmissionApplicationResponse;
import com.edu.kh.school.features.admission.dto.ApproveAdmissionRequest;
import com.edu.kh.school.features.admission.dto.ReviewAdmissionRequest;
import com.edu.kh.school.features.admission.dto.SubmitAdmissionRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface AdmissionService {

    AdmissionApplicationResponse submitApplication(SubmitAdmissionRequest request);

    AdmissionApplicationResponse getApplicationById(UUID id);

    AdmissionApplicationResponse getApplicationByNumber(String applicationNumber);

    Page<AdmissionApplicationResponse> searchApplications(
            String keyword, UUID academicYearId, AdmissionStatus status, Pageable pageable
    );

    AdmissionApplicationResponse reviewApplication(UUID id, ReviewAdmissionRequest request, String reviewedBy);

    AdmissionApplicationResponse approveApplication(UUID id, ApproveAdmissionRequest request, String reviewedBy);
}
