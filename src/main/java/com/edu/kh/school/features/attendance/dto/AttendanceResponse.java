package com.edu.kh.school.features.attendance.dto;

import com.edu.kh.school.features.attendance.AttendanceStatus;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record AttendanceResponse(
        UUID id,
        UUID studentId,
        String studentCode,
        String studentName,
        UUID classId,
        String className,
        LocalDate date,
        AttendanceStatus status,
        String remarks,
        String recordedBy,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
