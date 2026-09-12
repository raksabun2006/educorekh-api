package com.edu.kh.school.features.subject.dto;

import com.edu.kh.school.features.subject.SubjectStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record UpdateSubjectRequest(
        @NotBlank(message = "Subject name is required")
        String name,

        String description,

        @NotNull(message = "Credit is required")
        @Min(value = 1, message = "Credit must be at least 1")
        Integer credit,

        @NotNull(message = "Status is required")
        SubjectStatus status
) {
}
