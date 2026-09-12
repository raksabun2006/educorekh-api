package com.edu.kh.school.features.attendance.dto;

import com.edu.kh.school.features.attendance.AttendanceStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDate;
import java.util.UUID;

@Builder
public record RecordAttendanceRequest(
        @NotNull(message = "Student ID is required")
        UUID studentId,

        @NotNull(message = "Class ID is required")
        UUID classId,

        @NotNull(message = "Date is required")
        LocalDate date,

        @NotNull(message = "Attendance status is required")
        AttendanceStatus status,

        String remarks
) {
}
