package com.edu.kh.school.features.attendance.dto;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record AttendanceTrendResponse(
        LocalDate date,
        long presentCount,
        long absentCount,
        long lateCount,
        long excusedCount,
        long totalRecords,
        double attendanceRatePercentage
) {
}
