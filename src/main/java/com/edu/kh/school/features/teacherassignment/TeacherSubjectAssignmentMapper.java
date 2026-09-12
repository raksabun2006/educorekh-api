package com.edu.kh.school.features.teacherassignment;

import com.edu.kh.school.features.academic.AcademicYear;
import com.edu.kh.school.features.schoolclass.SchoolClass;
import com.edu.kh.school.features.subject.Subject;
import com.edu.kh.school.features.teacher.Teacher;
import com.edu.kh.school.features.teacherassignment.dto.TeacherSubjectAssignmentResponse;
import org.springframework.stereotype.Component;

@Component
public class TeacherSubjectAssignmentMapper {

    public TeacherSubjectAssignment toEntity(Teacher teacher, Subject subject, SchoolClass schoolClass, AcademicYear academicYear) {
        return TeacherSubjectAssignment.builder()
                .teacher(teacher)
                .subject(subject)
                .schoolClass(schoolClass)
                .academicYear(academicYear)
                .build();
    }

    public TeacherSubjectAssignmentResponse toDto(TeacherSubjectAssignment assignment) {
        return TeacherSubjectAssignmentResponse.builder()
                .id(assignment.getId())
                .teacherId(assignment.getTeacher().getId())
                .teacherCode(assignment.getTeacher().getTeacherCode())
                .teacherName(assignment.getTeacher().getFullName())
                .subjectId(assignment.getSubject().getId())
                .subjectCode(assignment.getSubject().getCode())
                .subjectName(assignment.getSubject().getName())
                .classId(assignment.getSchoolClass().getId())
                .className(assignment.getSchoolClass().getName())
                .academicYearId(assignment.getAcademicYear().getId())
                .academicYearName(assignment.getAcademicYear().getName())
                .createdAt(assignment.getCreatedAt())
                .updatedAt(assignment.getUpdatedAt())
                .build();
    }
}
