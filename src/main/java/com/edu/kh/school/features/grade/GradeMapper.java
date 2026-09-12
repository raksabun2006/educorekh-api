package com.edu.kh.school.features.grade;

import com.edu.kh.school.features.academic.AcademicYear;
import com.edu.kh.school.features.grade.dto.GradeResponse;
import com.edu.kh.school.features.grade.dto.RecordGradeRequest;
import com.edu.kh.school.features.student.Student;
import com.edu.kh.school.features.subject.Subject;
import org.springframework.stereotype.Component;

@Component
public class GradeMapper {

    public Grade toEntity(RecordGradeRequest request, Student student, Subject subject, AcademicYear academicYear, String recordedBy) {
        return Grade.builder()
                .student(student)
                .subject(subject)
                .academicYear(academicYear)
                .semester(request.semester())
                .score(request.score())
                .remarks(request.remarks())
                .recordedBy(recordedBy)
                .build();
    }

    public GradeResponse toDto(Grade grade) {
        return GradeResponse.builder()
                .id(grade.getId())
                .studentId(grade.getStudent().getId())
                .studentCode(grade.getStudent().getStudentCode())
                .studentName(grade.getStudent().getFullName())
                .subjectId(grade.getSubject().getId())
                .subjectCode(grade.getSubject().getCode())
                .subjectName(grade.getSubject().getName())
                .subjectCredit(grade.getSubject().getCredit())
                .academicYearId(grade.getAcademicYear().getId())
                .academicYearName(grade.getAcademicYear().getName())
                .semester(grade.getSemester())
                .score(grade.getScore())
                .gradeLetter(grade.getGradeLetter())
                .remarks(grade.getRemarks())
                .recordedBy(grade.getRecordedBy())
                .createdAt(grade.getCreatedAt())
                .updatedAt(grade.getUpdatedAt())
                .build();
    }
}
