package com.edu.kh.school.features.schedule.dto;

import lombok.Builder;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Builder
public record ClassScheduleResponse(
        UUID id,
        UUID classId,
        String className,
        UUID subjectId,
        String subjectCode,
        String subjectName,
        UUID teacherId,
        String teacherCode,
        String teacherName,
        String room,
        DayOfWeek dayOfWeek,
        LocalTime startTime,
        LocalTime endTime,
        UUID academicYearId,
        String academicYearName,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
