package com.edu.kh.school.features.parent.dto;

import com.edu.kh.school.features.parent.ParentStatus;
import lombok.Builder;

import java.util.UUID;

@Builder
public record ParentSummaryResponse(
        UUID id,
        String fullName,
        String email,
        String phone,
        ParentStatus status
) {
}
