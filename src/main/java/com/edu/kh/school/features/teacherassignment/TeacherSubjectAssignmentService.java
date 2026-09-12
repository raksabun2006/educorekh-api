package com.edu.kh.school.features.teacherassignment;

import com.edu.kh.school.features.teacherassignment.dto.AssignTeacherSubjectRequest;
import com.edu.kh.school.features.teacherassignment.dto.TeacherSubjectAssignmentResponse;

import java.util.List;
import java.util.UUID;

public interface TeacherSubjectAssignmentService {

    TeacherSubjectAssignmentResponse assignTeacherToSubject(AssignTeacherSubjectRequest request);

    void removeTeacherFromSubject(UUID assignmentId);

    List<TeacherSubjectAssignmentResponse> getTeacherSubjects(UUID teacherId, UUID academicYearId);

    List<TeacherSubjectAssignmentResponse> getClassSubjects(UUID classId, UUID academicYearId);

    List<TeacherSubjectAssignmentResponse> getSubjectTeachers(UUID subjectId, UUID academicYearId);
}
