package com.edu.kh.school.features.grade.dto;

import com.edu.kh.school.features.grade.Semester;
import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder
public record ClassResultResponse(
        UUID classId,
        String className,
        UUID academicYearId,
        String academicYearName,
        Semester semester,
        List<StudentSemesterResultResponse> studentResults,
        double classAverageScore
) {
}
