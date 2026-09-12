package com.edu.kh.school.features.grade.dto;

import com.edu.kh.school.features.grade.Semester;
import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder
public record StudentSemesterResultResponse(
        UUID studentId,
        String studentCode,
        String studentName,
        UUID academicYearId,
        String academicYearName,
        Semester semester,
        List<GradeResponse> grades,
        double averageScore,
        double gpa,
        String overallGrade
) {
}
