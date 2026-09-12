package com.edu.kh.school.features.academic.dto;

import com.edu.kh.school.features.academic.AcademicYearStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record UpdateAcademicYearRequest(
        @NotBlank(message = "Academic year name is required")
        String name,

        @NotNull(message = "Start date is required")
        LocalDate startDate,

        @NotNull(message = "End date is required")
        LocalDate endDate,

        @NotNull(message = "Status is required")
        AcademicYearStatus status
) {
}
