package com.edu.kh.school.features.teacherassignment.dto;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record TeacherSubjectAssignmentResponse(
        UUID id,
        UUID teacherId,
        String teacherCode,
        String teacherName,
        UUID subjectId,
        String subjectCode,
        String subjectName,
        UUID classId,
        String className,
        UUID academicYearId,
        String academicYearName,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
