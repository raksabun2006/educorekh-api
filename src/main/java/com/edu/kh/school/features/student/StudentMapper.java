package com.edu.kh.school.features.student;

import com.edu.kh.school.features.schoolclass.SchoolClass;
import com.edu.kh.school.features.student.dto.CreateStudentRequest;
import com.edu.kh.school.features.student.dto.StudentResponse;
import com.edu.kh.school.features.student.dto.StudentSummaryResponse;
import org.springframework.stereotype.Component;

@Component
public class StudentMapper {

    public Student toEntity(CreateStudentRequest request, SchoolClass schoolClass) {
        return Student.builder()
                .studentCode(request.studentCode().trim().toUpperCase())
                .fullName(request.fullName().trim())
                .email(request.email().trim().toLowerCase())
                .phone(request.phone() != null ? request.phone().trim() : null)
                .dateOfBirth(request.dateOfBirth())
                .sex(request.sex())
                .address(request.address() != null ? request.address().trim() : null)
                .admissionDate(request.admissionDate())
                .currentClass(schoolClass)
                .status(StudentStatus.ACTIVE)
                .build();
    }

    public StudentResponse toDto(Student student) {
        return StudentResponse.builder()
                .id(student.getId())
                .studentCode(student.getStudentCode())
                .fullName(student.getFullName())
                .email(student.getEmail())
                .phone(student.getPhone())
                .dateOfBirth(student.getDateOfBirth())
                .sex(student.getSex())
                .address(student.getAddress())
                .admissionDate(student.getAdmissionDate())
                .status(student.getStatus())
                .userId(student.getUser() != null ? student.getUser().getId() : null)
                .classId(student.getCurrentClass() != null ? student.getCurrentClass().getId() : null)
                .className(student.getCurrentClass() != null ? student.getCurrentClass().getName() : null)
                .createdAt(student.getCreatedAt())
                .updatedAt(student.getUpdatedAt())
                .build();
    }

    public StudentSummaryResponse toSummaryDto(Student student) {
        return StudentSummaryResponse.builder()
                .id(student.getId())
                .studentCode(student.getStudentCode())
                .fullName(student.getFullName())
                .email(student.getEmail())
                .className(student.getCurrentClass() != null ? student.getCurrentClass().getName() : null)
                .status(student.getStatus())
                .build();
    }
}
