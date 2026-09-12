package com.edu.kh.school.features.schoolclass.dto;

import com.edu.kh.school.features.schoolclass.ClassStatus;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record ClassResponse(
        UUID id,
        String name,
        String gradeLevel,
        UUID academicYearId,
        String academicYearName,
        String room,
        Integer capacity,
        UUID classTeacherId,
        String classTeacherName,
        ClassStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
