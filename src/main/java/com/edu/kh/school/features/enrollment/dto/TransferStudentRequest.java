package com.edu.kh.school.features.enrollment.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.UUID;

@Builder
public record TransferStudentRequest(
        @NotNull(message = "Target class ID is required")
        UUID targetClassId,

        String remarks
) {
}
