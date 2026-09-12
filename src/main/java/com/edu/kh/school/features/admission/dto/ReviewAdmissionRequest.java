package com.edu.kh.school.features.admission.dto;

import com.edu.kh.school.features.admission.AdmissionStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record ReviewAdmissionRequest(
        @NotNull(message = "Status is required")
        AdmissionStatus status,

        String reviewNotes
) {
}
