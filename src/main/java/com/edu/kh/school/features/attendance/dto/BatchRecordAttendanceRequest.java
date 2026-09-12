package com.edu.kh.school.features.attendance.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Builder
public record BatchRecordAttendanceRequest(
        @NotNull(message = "Class ID is required")
        UUID classId,

        @NotNull(message = "Date is required")
        LocalDate date,

        @NotEmpty(message = "Attendance list cannot be empty")
        @Valid
        List<StudentAttendanceItem> attendances
) {
}
