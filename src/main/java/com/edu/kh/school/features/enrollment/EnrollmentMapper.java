package com.edu.kh.school.features.enrollment;

import com.edu.kh.school.features.academic.AcademicYear;
import com.edu.kh.school.features.enrollment.dto.EnrollStudentRequest;
import com.edu.kh.school.features.enrollment.dto.EnrollmentResponse;
import com.edu.kh.school.features.schoolclass.SchoolClass;
import com.edu.kh.school.features.student.Student;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class EnrollmentMapper {

    public Enrollment toEntity(EnrollStudentRequest request, Student student, SchoolClass schoolClass, AcademicYear academicYear) {
        return Enrollment.builder()
                .student(student)
                .schoolClass(schoolClass)
                .academicYear(academicYear)
                .enrollmentDate(request.enrollmentDate() != null ? request.enrollmentDate() : LocalDate.now())
                .status(EnrollmentStatus.ACTIVE)
                .remarks(request.remarks())
                .build();
    }

    public EnrollmentResponse toDto(Enrollment enrollment) {
        return EnrollmentResponse.builder()
                .id(enrollment.getId())
                .studentId(enrollment.getStudent().getId())
                .studentCode(enrollment.getStudent().getStudentCode())
                .studentName(enrollment.getStudent().getFullName())
                .classId(enrollment.getSchoolClass().getId())
                .className(enrollment.getSchoolClass().getName())
                .academicYearId(enrollment.getAcademicYear().getId())
                .academicYearName(enrollment.getAcademicYear().getName())
                .enrollmentDate(enrollment.getEnrollmentDate())
                .status(enrollment.getStatus())
                .remarks(enrollment.getRemarks())
                .createdAt(enrollment.getCreatedAt())
                .updatedAt(enrollment.getUpdatedAt())
                .build();
    }
}
