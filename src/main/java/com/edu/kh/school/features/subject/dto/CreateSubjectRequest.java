package com.edu.kh.school.features.subject.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record CreateSubjectRequest(
        @NotBlank(message = "Subject code is required (e.g. MATH101)")
        String code,

        @NotBlank(message = "Subject name is required (e.g. Mathematics)")
        String name,

        String description,

        @NotNull(message = "Credit is required")
        @Min(value = 1, message = "Credit must be at least 1")
        Integer credit
) {
}
