package com.edu.kh.school.features.subject.dto;

import com.edu.kh.school.features.subject.SubjectStatus;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record SubjectResponse(
        UUID id,
        String code,
        String name,
        String description,
        Integer credit,
        SubjectStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
