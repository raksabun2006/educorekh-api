package com.edu.kh.school.features.admission.dto;

import lombok.Builder;

import java.time.LocalDate;
import java.util.UUID;

@Builder
public record ApproveAdmissionRequest(
        String studentCode, // Optional: if null, auto-generated
        UUID classId,       // Optional: assign to class immediately
        LocalDate admissionDate,
        String reviewNotes
) {
}
