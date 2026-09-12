package com.edu.kh.school.features.teacher;

import com.edu.kh.school.features.teacher.dto.CreateTeacherRequest;
import com.edu.kh.school.features.teacher.dto.TeacherResponse;
import com.edu.kh.school.features.teacher.dto.TeacherSummaryResponse;
import org.springframework.stereotype.Component;

@Component
public class TeacherMapper {

    public Teacher toEntity(CreateTeacherRequest request) {
        return Teacher.builder()
                .teacherCode(request.teacherCode().trim().toUpperCase())
                .fullName(request.fullName().trim())
                .email(request.email().trim().toLowerCase())
                .phone(request.phone() != null ? request.phone().trim() : null)
                .sex(request.sex())
                .dateOfBirth(request.dateOfBirth())
                .specialization(request.specialization() != null ? request.specialization().trim() : null)
                .hireDate(request.hireDate())
                .status(TeacherStatus.ACTIVE)
                .build();
    }

    public TeacherResponse toDto(Teacher teacher) {
        return TeacherResponse.builder()
                .id(teacher.getId())
                .teacherCode(teacher.getTeacherCode())
                .fullName(teacher.getFullName())
                .email(teacher.getEmail())
                .phone(teacher.getPhone())
                .sex(teacher.getSex())
                .dateOfBirth(teacher.getDateOfBirth())
                .specialization(teacher.getSpecialization())
                .hireDate(teacher.getHireDate())
                .status(teacher.getStatus())
                .userId(teacher.getUser() != null ? teacher.getUser().getId() : null)
                .createdAt(teacher.getCreatedAt())
                .updatedAt(teacher.getUpdatedAt())
                .build();
    }

    public TeacherSummaryResponse toSummaryDto(Teacher teacher) {
        return TeacherSummaryResponse.builder()
                .id(teacher.getId())
                .teacherCode(teacher.getTeacherCode())
                .fullName(teacher.getFullName())
                .email(teacher.getEmail())
                .specialization(teacher.getSpecialization())
                .status(teacher.getStatus())
                .build();
    }
}
