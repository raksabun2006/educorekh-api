package com.edu.kh.school.features.grade.dto;

import com.edu.kh.school.features.grade.Semester;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.UUID;

@Builder
public record RecordGradeRequest(
        @NotNull(message = "Student ID is required")
        UUID studentId,

        @NotNull(message = "Subject ID is required")
        UUID subjectId,

        @NotNull(message = "Academic year ID is required")
        UUID academicYearId,

        @NotNull(message = "Semester is required")
        Semester semester,

        @NotNull(message = "Score is required")
        @DecimalMin(value = "0.0", message = "Score must be at least 0")
        @DecimalMax(value = "100.0", message = "Score cannot exceed 100")
        Double score,

        String remarks
) {
}
