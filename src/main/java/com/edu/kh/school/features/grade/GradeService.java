package com.edu.kh.school.features.grade;

import com.edu.kh.school.features.grade.dto.*;

import java.util.List;
import java.util.UUID;

public interface GradeService {

    GradeResponse recordGrade(RecordGradeRequest request, String recordedBy);

    GradeResponse updateGrade(UUID id, UpdateGradeRequest request);

    List<GradeResponse> getStudentGrades(UUID studentId, UUID academicYearId, Semester semester);

    StudentSemesterResultResponse getStudentSemesterResult(UUID studentId, UUID academicYearId, Semester semester);

    ClassResultResponse getClassResults(UUID classId, UUID academicYearId, Semester semester);
}
