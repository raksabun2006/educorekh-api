package com.edu.kh.school.features.attendance.dto;

import com.edu.kh.school.features.attendance.AttendanceStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.UUID;

@Builder
public record StudentAttendanceItem(
        @NotNull(message = "Student ID is required")
        UUID studentId,

        @NotNull(message = "Attendance status is required")
        AttendanceStatus status,

        String remarks
) {
}
