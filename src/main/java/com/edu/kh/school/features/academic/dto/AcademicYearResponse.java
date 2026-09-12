package com.edu.kh.school.features.academic.dto;

import com.edu.kh.school.features.academic.AcademicYearStatus;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record AcademicYearResponse(
        UUID id,
        String name,
        LocalDate startDate,
        LocalDate endDate,
        AcademicYearStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
