package com.edu.kh.school.features.admission;

import com.edu.kh.school.features.admission.dto.AdmissionApplicationResponse;
import com.edu.kh.school.features.admission.dto.ApproveAdmissionRequest;
import com.edu.kh.school.features.admission.dto.ReviewAdmissionRequest;
import com.edu.kh.school.features.admission.dto.SubmitAdmissionRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admissions")
@RequiredArgsConstructor
public class AdmissionController {

    private final AdmissionService admissionService;

    // Public endpoint for applicants to submit an admission request
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AdmissionApplicationResponse submitApplication(@Valid @RequestBody SubmitAdmissionRequest request) {
        return admissionService.submitApplication(request);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public AdmissionApplicationResponse getApplicationById(@PathVariable UUID id) {
        return admissionService.getApplicationById(id);
    }

    @GetMapping("/number/{applicationNumber}")
    @ResponseStatus(HttpStatus.OK)
    public AdmissionApplicationResponse getApplicationByNumber(@PathVariable String applicationNumber) {
        return admissionService.getApplicationByNumber(applicationNumber);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ADMIN', 'PRINCIPAL', 'STAFF')")
    public Page<AdmissionApplicationResponse> searchApplications(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) UUID academicYearId,
            @RequestParam(required = false) AdmissionStatus status,
            Pageable pageable
    ) {
        return admissionService.searchApplications(keyword, academicYearId, status, pageable);
    }

    @PatchMapping("/{id}/review")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ADMIN', 'PRINCIPAL', 'STAFF')")
    public AdmissionApplicationResponse reviewApplication(
            @PathVariable UUID id,
            @Valid @RequestBody ReviewAdmissionRequest request,
            Authentication authentication
    ) {
        String reviewedBy = authentication != null ? authentication.getName() : "STAFF";
        return admissionService.reviewApplication(id, request, reviewedBy);
    }

    @PostMapping("/{id}/approve")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ADMIN', 'PRINCIPAL')")
    public AdmissionApplicationResponse approveApplication(
            @PathVariable UUID id,
            @RequestBody(required = false) ApproveAdmissionRequest request,
            Authentication authentication
    ) {
        String reviewedBy = authentication != null ? authentication.getName() : "ADMIN";
        return admissionService.approveApplication(id, request, reviewedBy);
    }
}
