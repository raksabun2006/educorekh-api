package com.edu.kh.school.features.schoolclass.dto;

import com.edu.kh.school.features.schoolclass.ClassStatus;
import lombok.Builder;

import java.util.UUID;

@Builder
public record ClassSummaryResponse(
        UUID id,
        String name,
        String gradeLevel,
        String room,
        ClassStatus status
) {
}
