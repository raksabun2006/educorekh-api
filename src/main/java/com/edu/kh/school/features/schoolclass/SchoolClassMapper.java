package com.edu.kh.school.features.schoolclass;

import com.edu.kh.school.features.academic.AcademicYear;
import com.edu.kh.school.features.schoolclass.dto.ClassResponse;
import com.edu.kh.school.features.schoolclass.dto.ClassSummaryResponse;
import com.edu.kh.school.features.schoolclass.dto.CreateClassRequest;
import com.edu.kh.school.features.teacher.Teacher;
import org.springframework.stereotype.Component;

@Component
public class SchoolClassMapper {

    public SchoolClass toEntity(CreateClassRequest request, AcademicYear academicYear, Teacher teacher) {
        return SchoolClass.builder()
                .name(request.name().trim())
                .gradeLevel(request.gradeLevel().trim())
                .academicYear(academicYear)
                .room(request.room() != null ? request.room().trim() : null)
                .capacity(request.capacity())
                .classTeacher(teacher)
                .status(ClassStatus.ACTIVE)
                .build();
    }

    public ClassResponse toDto(SchoolClass schoolClass) {
        return ClassResponse.builder()
                .id(schoolClass.getId())
                .name(schoolClass.getName())
                .gradeLevel(schoolClass.getGradeLevel())
                .academicYearId(schoolClass.getAcademicYear() != null ? schoolClass.getAcademicYear().getId() : null)
                .academicYearName(schoolClass.getAcademicYear() != null ? schoolClass.getAcademicYear().getName() : null)
                .room(schoolClass.getRoom())
                .capacity(schoolClass.getCapacity())
                .classTeacherId(schoolClass.getClassTeacher() != null ? schoolClass.getClassTeacher().getId() : null)
                .classTeacherName(schoolClass.getClassTeacher() != null ? schoolClass.getClassTeacher().getFullName() : null)
                .status(schoolClass.getStatus())
                .createdAt(schoolClass.getCreatedAt())
                .updatedAt(schoolClass.getUpdatedAt())
                .build();
    }

    public ClassSummaryResponse toSummaryDto(SchoolClass schoolClass) {
        return ClassSummaryResponse.builder()
                .id(schoolClass.getId())
                .name(schoolClass.getName())
                .gradeLevel(schoolClass.getGradeLevel())
                .room(schoolClass.getRoom())
                .status(schoolClass.getStatus())
                .build();
    }
}
