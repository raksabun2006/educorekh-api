package com.edu.kh.school.features.student.dto;

import com.edu.kh.school.features.auth.Sex;
import com.edu.kh.school.features.student.StudentStatus;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record StudentResponse(
        UUID id,
        String studentCode,
        String fullName,
        String email,
        String phone,
        LocalDate dateOfBirth,
        Sex sex,
        String address,
        LocalDate admissionDate,
        StudentStatus status,
        UUID userId,
        UUID classId,
        String className,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
