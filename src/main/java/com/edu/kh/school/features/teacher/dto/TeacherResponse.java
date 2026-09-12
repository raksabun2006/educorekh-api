package com.edu.kh.school.features.teacher.dto;

import com.edu.kh.school.features.auth.Sex;
import com.edu.kh.school.features.teacher.TeacherStatus;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record TeacherResponse(
        UUID id,
        String teacherCode,
        String fullName,
        String email,
        String phone,
        Sex sex,
        LocalDate dateOfBirth,
        String specialization,
        LocalDate hireDate,
        TeacherStatus status,
        UUID userId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
