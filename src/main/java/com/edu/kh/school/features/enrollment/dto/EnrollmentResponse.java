package com.edu.kh.school.features.enrollment.dto;

import com.edu.kh.school.features.enrollment.EnrollmentStatus;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record EnrollmentResponse(
        UUID id,
        UUID studentId,
        String studentCode,
        String studentName,
        UUID classId,
        String className,
        UUID academicYearId,
        String academicYearName,
        LocalDate enrollmentDate,
        EnrollmentStatus status,
        String remarks,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
