package com.edu.kh.school.features.schoolclass.dto;

import com.edu.kh.school.features.schoolclass.ClassStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.UUID;

@Builder
public record UpdateClassRequest(
        @NotBlank(message = "Class name is required")
        String name,

        @NotBlank(message = "Grade level is required")
        String gradeLevel,

        @NotNull(message = "Academic year id is required")
        UUID academicYearId,

        String room,

        @NotNull(message = "Capacity is required")
        @Min(value = 1, message = "Capacity must be greater than 0")
        Integer capacity,

        UUID classTeacherId,

        @NotNull(message = "Status is required")
        ClassStatus status
) {
}
