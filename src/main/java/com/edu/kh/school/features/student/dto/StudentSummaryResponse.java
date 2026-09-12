package com.edu.kh.school.features.student.dto;

import com.edu.kh.school.features.student.StudentStatus;
import lombok.Builder;

import java.util.UUID;

@Builder
public record StudentSummaryResponse(
        UUID id,
        String studentCode,
        String fullName,
        String email,
        String className,
        StudentStatus status
) {
}
