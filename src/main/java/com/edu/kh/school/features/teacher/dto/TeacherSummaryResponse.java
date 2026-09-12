package com.edu.kh.school.features.teacher.dto;

import com.edu.kh.school.features.teacher.TeacherStatus;
import lombok.Builder;

import java.util.UUID;

@Builder
public record TeacherSummaryResponse(
        UUID id,
        String teacherCode,
        String fullName,
        String email,
        String specialization,
        TeacherStatus status
) {
}
