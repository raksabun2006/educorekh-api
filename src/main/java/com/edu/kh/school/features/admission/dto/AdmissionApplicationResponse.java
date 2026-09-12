package com.edu.kh.school.features.admission.dto;

import com.edu.kh.school.features.admission.AdmissionStatus;
import com.edu.kh.school.features.auth.Sex;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record AdmissionApplicationResponse(
        UUID id,
        String applicationNumber,
        String applicantFullName,
        String applicantEmail,
        String applicantPhone,
        LocalDate dateOfBirth,
        Sex sex,
        String address,
        String previousSchool,
        String applyingForGrade,
        UUID academicYearId,
        String academicYearName,
        AdmissionStatus status,
        String reviewNotes,
        String reviewedBy,
        UUID createdStudentId,
        String createdStudentCode,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
