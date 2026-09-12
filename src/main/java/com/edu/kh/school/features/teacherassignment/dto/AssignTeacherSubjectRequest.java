package com.edu.kh.school.features.teacherassignment.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.UUID;

@Builder
public record AssignTeacherSubjectRequest(
        @NotNull(message = "Teacher ID is required")
        UUID teacherId,

        @NotNull(message = "Subject ID is required")
        UUID subjectId,

        @NotNull(message = "Class ID is required")
        UUID classId,

        @NotNull(message = "Academic year ID is required")
        UUID academicYearId
) {
}
