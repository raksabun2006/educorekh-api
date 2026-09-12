package com.edu.kh.school.features.schoolclass.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.UUID;

@Builder
public record CreateClassRequest(
        @NotBlank(message = "Class name is required (e.g. Grade 10A)")
        String name,

        @NotBlank(message = "Grade level is required (e.g. 10)")
        String gradeLevel,

        @NotNull(message = "Academic year id is required")
        UUID academicYearId,

        String room,

        @NotNull(message = "Capacity is required")
        @Min(value = 1, message = "Capacity must be greater than 0")
        Integer capacity,

        UUID classTeacherId
) {
}
