package com.edu.kh.school.features.enrollment.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDate;
import java.util.UUID;

@Builder
public record EnrollStudentRequest(
        @NotNull(message = "Student ID is required")
        UUID studentId,

        @NotNull(message = "Class ID is required")
        UUID classId,

        @NotNull(message = "Academic year ID is required")
        UUID academicYearId,

        LocalDate enrollmentDate,

        String remarks
) {
}
