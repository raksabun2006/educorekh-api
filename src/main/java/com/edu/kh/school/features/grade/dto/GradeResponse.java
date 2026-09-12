package com.edu.kh.school.features.grade.dto;

import com.edu.kh.school.features.grade.Semester;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record GradeResponse(
        UUID id,
        UUID studentId,
        String studentCode,
        String studentName,
        UUID subjectId,
        String subjectCode,
        String subjectName,
        Integer subjectCredit,
        UUID academicYearId,
        String academicYearName,
        Semester semester,
        Double score,
        String gradeLetter,
        String remarks,
        String recordedBy,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
