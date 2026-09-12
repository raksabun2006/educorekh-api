package com.edu.kh.school.features.subject.dto;

import com.edu.kh.school.features.subject.SubjectStatus;
import lombok.Builder;

import java.util.UUID;

@Builder
public record SubjectSummaryResponse(
        UUID id,
        String code,
        String name,
        Integer credit,
        SubjectStatus status
) {
}
