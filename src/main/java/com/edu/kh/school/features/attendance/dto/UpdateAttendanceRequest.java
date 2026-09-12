package com.edu.kh.school.features.attendance.dto;

import com.edu.kh.school.features.attendance.AttendanceStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record UpdateAttendanceRequest(
        @NotNull(message = "Attendance status is required")
        AttendanceStatus status,

        String remarks
) {
}
