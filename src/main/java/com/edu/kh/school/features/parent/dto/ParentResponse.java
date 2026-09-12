package com.edu.kh.school.features.parent.dto;

import com.edu.kh.school.features.parent.ParentStatus;
import com.edu.kh.school.features.student.dto.StudentSummaryResponse;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder
public record ParentResponse(
        UUID id,
        String fullName,
        String email,
        String phone,
        String occupation,
        String address,
        ParentStatus status,
        List<StudentSummaryResponse> children,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
