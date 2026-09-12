package com.edu.kh.school.features.schedule.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.UUID;

@Builder
public record UpdateScheduleRequest(
        @NotNull(message = "Class ID is required")
        UUID classId,

        @NotNull(message = "Subject ID is required")
        UUID subjectId,

        @NotNull(message = "Teacher ID is required")
        UUID teacherId,

        @NotBlank(message = "Room is required")
        String room,

        @NotNull(message = "Day of week is required")
        DayOfWeek dayOfWeek,

        @NotNull(message = "Start time is required")
        LocalTime startTime,

        @NotNull(message = "End time is required")
        LocalTime endTime,

        @NotNull(message = "Academic year ID is required")
        UUID academicYearId
) {
}
