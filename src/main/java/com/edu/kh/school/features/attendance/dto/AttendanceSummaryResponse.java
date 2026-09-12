package com.edu.kh.school.features.attendance.dto;

import lombok.Builder;

import java.time.LocalDate;
import java.util.UUID;

@Builder
public record AttendanceSummaryResponse(
        UUID classId,
        String className,
        LocalDate startDate,
        LocalDate endDate,
        long totalRecords,
        long presentCount,
        long absentCount,
        long lateCount,
        long excusedCount,
        double attendanceRatePercentage
) {
}
